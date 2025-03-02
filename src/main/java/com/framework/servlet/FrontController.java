package com.framework.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.framework.annotation.*;
import com.framework.modelview.ModelView;
import com.framework.error.FrameworkException;
import com.framework.binding.RequestParamBinder;
import com.framework.session.Session;
import com.framework.session.SessionManager;
import com.framework.http.HttpVerb;
import com.framework.mapping.Mapping;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * FrontController - Contrôleur principal du framework
 */
public class FrontController extends HttpServlet {
    private static final Map<String, Mapping> urlMappings = new HashMap<>();
    private static final Map<Class<?>, Object> controllerInstances = new HashMap<>();
    private static final Map<Class<?>, Field> sessionFields = new HashMap<>();
    private static final Gson gson = new Gson();

    @Override
    public void init() throws ServletException {
        System.out.println("FrontController - Initialisation...");
        String packageName = getInitParameter("controllerPackage");
        System.out.println("Package à scanner : " + packageName);
        
        if (packageName == null || packageName.trim().isEmpty()) {
            throw new ServletException("Le package des contrôleurs n'est pas spécifié dans web.xml");
        }
        
        try {
            scanControllers(packageName.trim());
            
            if (urlMappings.isEmpty()) {
                throw FrameworkException.emptyControllerPackage(packageName);
            }
            
            System.out.println("\nMappings trouvés :");
            for (Map.Entry<String, Mapping> entry : urlMappings.entrySet()) {
                System.out.println(entry.getKey() + " -> " + entry.getValue().toString());
            }
        } catch (Exception e) {
            throw new ServletException("Error initializing FrontController", e);
        }
    }

    private void scanControllers(String basePackage) throws FrameworkException {
        try {
            System.out.println("Début du scan du package : " + basePackage);
            
            ConfigurationBuilder config = new ConfigurationBuilder()
                .setUrls(ClasspathHelper.forPackage(basePackage))
                .setScanners(Scanners.TypesAnnotated, Scanners.SubTypes)
                .filterInputsBy(new FilterBuilder().includePackage(basePackage));
            
            Reflections reflections = new Reflections(config);
            
            Set<Class<?>> controllers = new HashSet<>();
            controllers.addAll(reflections.getTypesAnnotatedWith(Controller.class));
            controllers.addAll(reflections.getTypesAnnotatedWith(RestController.class));
            
            for (Class<?> controller : controllers) {
                Object instance = controller.getDeclaredConstructor().newInstance();
                controllerInstances.put(controller, instance);
                
                // Recherche du champ Session à injecter
                for (Field field : controller.getDeclaredFields()) {
                    if (field.isAnnotationPresent(SessionInject.class)) {
                        field.setAccessible(true);
                        sessionFields.put(controller, field);
                        break;
                    }
                }
                
                // Scan des méthodes pour les différents types de mapping
                for (Method method : controller.getDeclaredMethods()) {
                    scanMethodMappings(method, controller);
                }
            }
        } catch (Exception e) {
            throw new FrameworkException("Erreur lors du scan des contrôleurs : " + e.getMessage(), 500);
        }
    }

    private void scanMethodMappings(Method method, Class<?> controller) throws FrameworkException {
        String url = null;
        HttpVerb verb = null;

        // GetMapping
        GetMapping getMapping = method.getAnnotation(GetMapping.class);
        if (getMapping != null) {
            url = getMapping.value();
            verb = HttpVerb.GET;
        }

        // PostMapping
        PostMapping postMapping = method.getAnnotation(PostMapping.class);
        if (postMapping != null) {
            if (url != null) {
                throw new FrameworkException("Multiple HTTP verb annotations on method: " + method.getName(), 500);
            }
            url = postMapping.value();
            verb = HttpVerb.POST;
        }

        // RestEndPoint (considéré comme GET par défaut)
        RestEndPoint restEndPoint = method.getAnnotation(RestEndPoint.class);
        if (restEndPoint != null) {
            if (url != null) {
                throw new FrameworkException("Multiple HTTP verb annotations on method: " + method.getName(), 500);
            }
            url = restEndPoint.value();
            verb = HttpVerb.GET;
        }

        if (url != null && verb != null) {
            if (!url.startsWith("/")) {
                url = "/" + url;
            }

            Mapping mapping = urlMappings.computeIfAbsent(url, Mapping::new);
            if (mapping.supportsVerb(verb)) {
                Class<?> existingController = mapping.getControllerClass(verb);
                throw FrameworkException.duplicateMapping(url, existingController.getSimpleName(), controller.getSimpleName());
            }

            mapping.addMethodMapping(verb, method, controller);
        }
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String method = request.getMethod();
        HttpVerb verb;
        try {
            verb = HttpVerb.fromString(method);
        } catch (IllegalArgumentException e) {
            response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "HTTP method not supported: " + method);
            return;
        }
        
        processRequest(request, response, verb);
    }

    private void processRequest(HttpServletRequest request, HttpServletResponse response, HttpVerb verb) 
            throws ServletException, IOException {
        try {
            String relativeUrl = getRelativeUrl(request);
            Mapping mapping = urlMappings.get(relativeUrl);
            
            if (mapping == null || !mapping.supportsVerb(verb)) {
                throw FrameworkException.urlNotFound(relativeUrl);
            }

            Method method = mapping.getMethod(verb);
            Class<?> controllerClass = mapping.getControllerClass(verb);
            Object controllerInstance = controllerInstances.get(controllerClass);

            // Injection de la session si nécessaire
            Field sessionField = sessionFields.get(controllerClass);
            if (sessionField != null) {
                Session session = SessionManager.getSession(request, response, true);
                sessionField.set(controllerInstance, session);
            }

            // Binding des paramètres et invocation
            Object[] args = RequestParamBinder.bindParameters(method, request);
            Object result = method.invoke(controllerInstance, args);

            handleResult(result, request, response, controllerClass, method);
        } catch (Exception e) {
            handleException(e, response);
        }
    }

    private void handleResult(Object result, HttpServletRequest request, HttpServletResponse response, 
                            Class<?> controllerClass, Method method) throws ServletException, IOException, FrameworkException {
        if (result == null) {
            throw FrameworkException.unsupportedReturnType(method.getName(), "null");
        }

        boolean isRestController = controllerClass.isAnnotationPresent(RestController.class);
        boolean isRestEndPoint = method.isAnnotationPresent(RestEndPoint.class);

        if (isRestController || isRestEndPoint) {
            sendJsonResponse(response, result);
        } else if (result instanceof ModelView) {
            handleModelView((ModelView) result, request, response);
        } else if (result instanceof String) {
            sendHtmlResponse(response, (String) result);
        } else {
            throw FrameworkException.unsupportedReturnType(method.getName(), result.getClass().getSimpleName());
        }
    }

    private void handleModelView(ModelView mv, HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        for (Map.Entry<String, Object> entry : mv.getData().entrySet()) {
            request.setAttribute(entry.getKey(), entry.getValue());
        }
        String viewPath = "/WEB-INF/views/" + mv.getUrl();
        request.getRequestDispatcher(viewPath).forward(request, response);
    }

    private void sendJsonResponse(HttpServletResponse response, Object result) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        if (result instanceof ModelView) {
            out.println(gson.toJson(((ModelView) result).getData()));
        } else {
            out.println(gson.toJson(result));
        }
    }

    private void sendHtmlResponse(HttpServletResponse response, String content) throws IOException {
        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        out.println(content);
    }

    private void handleException(Exception e, HttpServletResponse response) throws IOException {
        if (e instanceof InvocationTargetException) {
            e = (Exception) e.getCause();
        }
        
        if (e instanceof FrameworkException) {
            FrameworkException fe = (FrameworkException) e;
            response.sendError(fe.getStatusCode(), fe.getMessage());
        } else {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                             "Erreur interne du serveur : " + e.getMessage());
        }
    }

    private String getRelativeUrl(HttpServletRequest request) {
        String contextPath = request.getContextPath();
        String servletPath = request.getServletPath();
        String requestUri = request.getRequestURI();
        
        String relativeUrl = requestUri.substring(contextPath.length() + servletPath.length());
        return relativeUrl.isEmpty() ? "/" : relativeUrl;
    }
}
