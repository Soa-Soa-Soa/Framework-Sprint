package com.framework.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.framework.annotation.Controller;
import com.framework.annotation.GetMapping;
import com.framework.modelview.ModelView;
import com.framework.error.FrameworkException;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.*;

public class FrontController extends HttpServlet {
    private static final Map<Class<?>, Object> controllerInstances = new HashMap<>();
    private static final Map<String, Method> getMappings = new HashMap<>();
    private static final Map<String, String> urlToControllerMap = new HashMap<>();

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
            
            // Vérifier si des contrôleurs ont été trouvés
            if (getMappings.isEmpty()) {
                throw FrameworkException.emptyControllerPackage(packageName);
            }
            
            System.out.println("\nMappings trouvés :");
            for (Map.Entry<String, Method> entry : getMappings.entrySet()) {
                System.out.println(entry.getKey() + " -> " + 
                                 entry.getValue().getDeclaringClass().getSimpleName() + "." + 
                                 entry.getValue().getName());
            }
        } catch (FrameworkException e) {
            throw new ServletException(e.getMessage(), e);
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
            
            Set<Class<?>> controllers = reflections.getTypesAnnotatedWith(Controller.class);
            System.out.println("Nombre de contrôleurs trouvés : " + controllers.size());
            
            // Vérifier si le package existe
            if (controllers.isEmpty() && ClasspathHelper.forPackage(basePackage).isEmpty()) {
                throw FrameworkException.controllerPackageNotFound(basePackage);
            }
            
            for (Class<?> controller : controllers) {
                System.out.println("Traitement du contrôleur : " + controller.getName());
                
                Object instance = controller.getDeclaredConstructor().newInstance();
                controllerInstances.put(controller, instance);
                
                for (Method method : controller.getDeclaredMethods()) {
                    GetMapping mapping = method.getAnnotation(GetMapping.class);
                    if (mapping != null) {
                        String url = mapping.value();
                        if (!url.startsWith("/")) {
                            url = "/" + url;
                        }
                        
                        // Vérifier les doublons d'URL
                        if (getMappings.containsKey(url)) {
                            String existingController = urlToControllerMap.get(url);
                            throw FrameworkException.duplicateMapping(
                                url, 
                                existingController,
                                controller.getSimpleName()
                            );
                        }
                        
                        System.out.println("Ajout du mapping : " + url + " -> " + method.getName());
                        getMappings.put(url, method);
                        urlToControllerMap.put(url, controller.getSimpleName());
                    }
                }
            }
        } catch (FrameworkException e) {
            throw e;
        } catch (Exception e) {
            System.out.println("Erreur lors du scan des contrôleurs :");
            e.printStackTrace();
            throw new FrameworkException(
                "Erreur lors du scan des contrôleurs : " + e.getMessage(),
                500
            );
        }
    }

    private String getRelativeUrl(HttpServletRequest request) {
        String contextPath = request.getContextPath();
        String servletPath = request.getServletPath();
        String requestUri = request.getRequestURI();
        
        String relativeUrl = requestUri.substring(contextPath.length() + servletPath.length());
        System.out.println("URL demandée : " + requestUri);
        System.out.println("Context Path : " + contextPath);
        System.out.println("Servlet Path : " + servletPath);
        System.out.println("URL relative : " + relativeUrl);
        
        return relativeUrl.isEmpty() ? "/" : relativeUrl;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String relativeUrl = getRelativeUrl(request);
            System.out.println("Recherche de la méthode pour l'URL : " + relativeUrl);
            
            Method method = getMappings.get(relativeUrl);
            if (method == null) {
                throw FrameworkException.urlNotFound(relativeUrl);
            }
            
            try {
                Object controllerInstance = controllerInstances.get(method.getDeclaringClass());
                System.out.println("Méthode trouvée : " + method.getName() + " dans " + method.getDeclaringClass().getSimpleName());
                
                Object result = method.invoke(controllerInstance);
                System.out.println("Résultat de l'invocation : " + result);
                
                if (result == null) {
                    throw FrameworkException.unsupportedReturnType(
                        method.getName(),
                        "null"
                    );
                }
                
                if (result instanceof ModelView) {
                    ModelView mv = (ModelView) result;
                    for (Map.Entry<String, Object> entry : mv.getData().entrySet()) {
                        request.setAttribute(entry.getKey(), entry.getValue());
                    }
                    String viewPath = "/WEB-INF/views/" + mv.getUrl();
                    System.out.println("Redirection vers : " + viewPath);
                    request.getRequestDispatcher(viewPath).forward(request, response);
                } else if (result instanceof String) {
                    response.setContentType("text/html");
                    response.setCharacterEncoding("UTF-8");
                    PrintWriter out = response.getWriter();
                    out.println(result.toString());
                } else {
                    throw FrameworkException.unsupportedReturnType(
                        method.getName(),
                        result.getClass().getSimpleName()
                    );
                }
            } catch (FrameworkException e) {
                throw e;
            } catch (Exception e) {
                throw new FrameworkException(
                    "Erreur lors de l'exécution de la méthode : " + e.getMessage(),
                    500
                );
            }
        } catch (FrameworkException e) {
            response.setStatus(e.getStatusCode());
            response.setContentType("text/html");
            response.setCharacterEncoding("UTF-8");
            PrintWriter out = response.getWriter();
            out.println("<html><body>");
            out.println("<h1>Erreur " + e.getStatusCode() + "</h1>");
            out.println("<p>" + e.getMessage() + "</p>");
            out.println("</body></html>");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
