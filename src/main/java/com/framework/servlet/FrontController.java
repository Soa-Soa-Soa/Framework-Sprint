package com.framework.servlet;

import com.framework.annotation.*;
import com.framework.binding.RequestParamBinder;
import com.framework.error.FrameworkException;
import com.framework.http.HttpVerb;
import com.framework.mapping.Mapping;
import com.framework.modelview.ModelView;
import com.framework.session.Session;
import com.framework.session.SessionManager;
import com.framework.validation.ValidationErrors;
import com.google.gson.Gson;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

@MultipartConfig(
    fileSizeThreshold = 1024 * 1024,      // 1 MB
    maxFileSize = 1024 * 1024 * 10,       // 10 MB
    maxRequestSize = 1024 * 1024 * 100     // 100 MB
)
public class FrontController extends HttpServlet {
    private static final Map<String, Mapping> urlMappings = new HashMap<>();
    private static final Map<Class<?>, Object> controllerInstances = new HashMap<>();
    private static final Map<Class<?>, Field> sessionFields = new HashMap<>();
    private static final Gson gson = new Gson();

    @Override
    public void init() throws ServletException {
        System.out.println("FrontController - Initialisation...");
        String packageName = getServletConfig().getInitParameter("controllerPackage");
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
    protected void service(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        try {
            String method = req.getMethod();
            HttpVerb verb;
            try {
                verb = HttpVerb.fromString(method);
            } catch (IllegalArgumentException e) {
                resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "HTTP method not supported: " + method);
                return;
            }
            
            processRequest(req, resp, verb);
        } catch (Exception e) {
            handleError(req, resp, e);
        }
    }

    private void processRequest(HttpServletRequest req, HttpServletResponse resp, HttpVerb verb) 
            throws ServletException, IOException {
        try {
            String relativeUrl = getRelativeUrl(req);
            Mapping mapping = urlMappings.get(relativeUrl);
            
            if (mapping == null) {
                throw FrameworkException.urlNotFound(relativeUrl);
            }
            
            if (!mapping.supportsVerb(verb)) {
                throw FrameworkException.verbNotSupported(verb, relativeUrl);
            }

            Method method = mapping.getMethod(verb);
            Class<?> controllerClass = mapping.getControllerClass(verb);
            Object controllerInstance = controllerInstances.get(controllerClass);

            // Injection de la session si nécessaire
            Field sessionField = sessionFields.get(controllerClass);
            if (sessionField != null) {
                Session session = SessionManager.getSession(req, resp, true);
                sessionField.set(controllerInstance, session);
            }

            // Binding des paramètres avec validation
            Object[] args = null;
            ValidationErrors validationErrors = new ValidationErrors();
            
            try {
                args = RequestParamBinder.bindParameters(method, req);
            } catch (FrameworkException e) {
                // En cas d'erreur de validation
                handleValidationError(method, req, resp, e);
                return;
            }
            
            // Appel de la méthode du contrôleur
            Object result = method.invoke(controllerInstance, args);
            
            // Traitement du résultat
            handleResult(result, req, resp, controllerClass, method);
        } catch (Exception e) {
            handleError(req, resp, e);
        }
    }

    private void handleResult(Object result, HttpServletRequest req, HttpServletResponse resp, 
                            Class<?> controllerClass, Method method) throws ServletException, IOException, FrameworkException {
        if (result == null) {
            throw FrameworkException.unsupportedReturnType(method.getName(), "null");
        }

        boolean isRestController = controllerClass.isAnnotationPresent(RestController.class);
        boolean isRestEndPoint = method.isAnnotationPresent(RestEndPoint.class);

        if (isRestController || isRestEndPoint) {
            sendJsonResponse(resp, result);
        } else if (result instanceof ModelView) {
            handleModelView((ModelView) result, req, resp);
        } else if (result instanceof String) {
            sendHtmlResponse(resp, (String) result);
        } else {
            throw FrameworkException.unsupportedReturnType(method.getName(), result.getClass().getSimpleName());
        }
    }

    private void handleModelView(ModelView mv, HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        // Ajoute les données au modèle
        for (Map.Entry<String, Object> entry : mv.getModel().entrySet()) {
            req.setAttribute(entry.getKey(), entry.getValue());
        }
        
        // Forward vers la vue
        req.getRequestDispatcher("/WEB-INF/views/" + mv.getView()).forward(req, resp);
    }

    private void sendJsonResponse(HttpServletResponse resp, Object result) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();
        if (result instanceof ModelView) {
            out.println(gson.toJson(((ModelView) result).getModel()));
        } else {
            out.println(gson.toJson(result));
        }
    }

    private void sendHtmlResponse(HttpServletResponse resp, String content) throws IOException {
        resp.setContentType("text/html");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();
        out.println(content);
    }

    private void handleValidationError(Method method, HttpServletRequest req, 
            HttpServletResponse resp, FrameworkException e) throws ServletException, IOException {
        // Récupère l'URL d'erreur depuis l'annotation
        ErrorURL errorURL = method.getAnnotation(ErrorURL.class);
        String redirectUrl = errorURL != null ? errorURL.value() : req.getRequestURI();
        
        // Stocke les erreurs et les valeurs soumises
        ValidationErrors errors = e.getValidationErrors();
        if (errors == null) {
            errors = new ValidationErrors();
        }
        
        // Stocke les valeurs soumises pour les réafficher
        Map<String, String[]> params = req.getParameterMap();
        for (Map.Entry<String, String[]> entry : params.entrySet()) {
            if (entry.getValue() != null && entry.getValue().length > 0) {
                errors.addValue(entry.getKey(), entry.getValue()[0]);
            }
        }
        
        // Ajoute les erreurs et valeurs à la requête
        req.setAttribute("errors", errors.getErrors());
        req.setAttribute("values", errors.getValues());
        
        // Redirige vers l'URL d'erreur
        req.getRequestDispatcher("/WEB-INF/views" + redirectUrl + ".jsp").forward(req, resp);
    }

    private void handleError(HttpServletRequest req, HttpServletResponse resp, Exception e) 
            throws ServletException, IOException {
        if (e instanceof FrameworkException) {
            FrameworkException fe = (FrameworkException) e;
            resp.sendError(fe.getStatusCode(), fe.getMessage());
        } else {
            e.printStackTrace();
            resp.sendError(500, "Une erreur interne est survenue");
        }
    }

    private String getRelativeUrl(HttpServletRequest req) {
        String contextPath = req.getContextPath();
        String servletPath = req.getServletPath();
        String requestUri = req.getRequestURI();
        
        String relativeUrl = requestUri.substring(contextPath.length() + servletPath.length());
        return relativeUrl.isEmpty() ? "/" : relativeUrl;
    }
}
