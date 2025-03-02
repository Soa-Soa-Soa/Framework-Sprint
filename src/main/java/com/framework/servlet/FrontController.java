package com.framework.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.framework.annotation.Controller;
import com.framework.annotation.GetMapping;
import com.framework.annotation.RestController;
import com.framework.annotation.RestEndPoint;
import com.framework.annotation.SessionInject;
import com.framework.modelview.ModelView;
import com.framework.error.FrameworkException;
import com.framework.binding.RequestParamBinder;
import com.framework.session.Session;
import com.framework.session.SessionManager;
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
 * Responsable de :
 * 1. La gestion des requêtes HTTP entrantes
 * 2. Le routage vers les contrôleurs appropriés
 * 3. L'injection des sessions
 * 4. La gestion des paramètres de requête
 * 5. Le rendu des vues ou réponses JSON
 */
public class FrontController extends HttpServlet {
    /** Map stockant les instances des contrôleurs pour éviter de les recréer à chaque requête */
    private static final Map<Class<?>, Object> controllerInstances = new HashMap<>();
    /** Map associant les URLs aux méthodes des contrôleurs */
    private static final Map<String, Method> getMappings = new HashMap<>();
    /** Map pour vérifier les doublons d'URL entre contrôleurs */
    private static final Map<String, String> urlToControllerMap = new HashMap<>();
    /** Cache des champs annotés avec @SessionInject pour chaque contrôleur */
    private static final Map<Class<?>, Field> sessionFields = new HashMap<>();
    /** Gson pour la sérialisation JSON */
    private static final Gson gson = new Gson();

    /**
     * Initialisation du FrontController
     * - Charge les contrôleurs depuis le package spécifié
     * - Analyse les annotations et crée les mappings URL
     * - Vérifie les configurations et les erreurs potentielles
     */
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

    /**
     * Analyse les contrôleurs dans le package spécifié
     * - Recherche les classes annotées avec @Controller ou @RestController
     * - Crée les instances des contrôleurs
     * - Identifie les champs de session à injecter
     * - Enregistre les mappings URL des méthodes
     * 
     * @param basePackage Le package à scanner pour trouver les contrôleurs
     * @throws FrameworkException Si une erreur survient pendant l'analyse
     */
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
            
            System.out.println("Nombre de contrôleurs trouvés : " + controllers.size());
            
            if (controllers.isEmpty() && ClasspathHelper.forPackage(basePackage).isEmpty()) {
                throw FrameworkException.controllerPackageNotFound(basePackage);
            }
            
            for (Class<?> controller : controllers) {
                System.out.println("Traitement du contrôleur : " + controller.getName());
                
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
                
                for (Method method : controller.getDeclaredMethods()) {
                    String url = null;
                    
                    GetMapping getMapping = method.getAnnotation(GetMapping.class);
                    RestEndPoint restEndPoint = method.getAnnotation(RestEndPoint.class);
                    
                    if (getMapping != null) {
                        url = getMapping.value();
                    } else if (restEndPoint != null) {
                        url = restEndPoint.value();
                    }
                    
                    if (url != null) {
                        if (!url.startsWith("/")) {
                            url = "/" + url;
                        }
                        
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

    /**
     * Extrait l'URL relative de la requête
     * Exemple:
     * - URL complète: http://localhost:8080/monapp/app/users
     * - Context path: /monapp
     * - Servlet path: /app
     * - URL relative retournée: /users
     */
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

    /**
     * Gère les requêtes HTTP GET
     * 1. Trouve la méthode correspondant à l'URL
     * 2. Injecte la session si nécessaire
     * 3. Lie les paramètres de la requête
     * 4. Exécute la méthode du contrôleur
     * 5. Gère le résultat (ModelView, String ou JSON)
     */
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
                Class<?> controllerClass = method.getDeclaringClass();
                Object controllerInstance = controllerInstances.get(controllerClass);
                System.out.println("Méthode trouvée : " + method.getName() + " dans " + controllerClass.getSimpleName());
                
                // Injection de la session si nécessaire
                Field sessionField = sessionFields.get(controllerClass);
                if (sessionField != null) {
                    Session session = SessionManager.getSession(request, response, true);
                    sessionField.set(controllerInstance, session);
                }
                
                // Binding des paramètres
                Object[] args = RequestParamBinder.bindParameters(method, request);
                Object result = method.invoke(controllerInstance, args);
                
                System.out.println("Résultat de l'invocation : " + result);
                
                if (result == null) {
                    throw FrameworkException.unsupportedReturnType(
                        method.getName(),
                        "null"
                    );
                }
                
                // Vérifie si c'est un point d'entrée REST
                boolean isRestController = controllerClass.isAnnotationPresent(RestController.class);
                boolean isRestEndPoint = method.isAnnotationPresent(RestEndPoint.class);
                
                if (isRestController || isRestEndPoint) {
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");
                    PrintWriter out = response.getWriter();
                    
                    if (result instanceof ModelView) {
                        // Si c'est un ModelView, on sérialise uniquement les données
                        ModelView mv = (ModelView) result;
                        out.println(gson.toJson(mv.getData()));
                    } else {
                        // Sinon on sérialise directement l'objet
                        out.println(gson.toJson(result));
                    }
                } else if (result instanceof ModelView) {
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
            } catch (InvocationTargetException e) {
                Throwable cause = e.getCause();
                if (cause instanceof FrameworkException) {
                    throw (FrameworkException) cause;
                }
                throw new FrameworkException(
                    "Erreur lors de l'invocation de la méthode : " + e.getMessage(),
                    500
                );
            }
        } catch (FrameworkException e) {
            response.sendError(e.getStatusCode(), e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(
                HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                "Erreur interne du serveur : " + e.getMessage()
            );
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
