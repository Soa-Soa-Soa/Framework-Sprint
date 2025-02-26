package com.framework.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.framework.annotation.Controller;
import com.framework.annotation.GetMapping;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.*;
import org.reflections.Reflections;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

public class FrontController extends HttpServlet {
    private List<Class<?>> controllers = new ArrayList<>();
    private Map<String, Method> getMappings = new HashMap<>();
    private Map<Method, Object> controllerInstances = new HashMap<>();

    @Override
    public void init() throws ServletException {
        System.out.println("FrontController - Initialisation...");
        String packageList = getInitParameter("controllerPackage");
        System.out.println("Package à scanner : " + packageList);
        
        if (packageList != null && !packageList.trim().isEmpty()) {
            String[] packages = packageList.split(",");
            for (String packageName : packages) {
                System.out.println("Scanning package : " + packageName.trim());
                scanControllersInPackage(packageName.trim());
            }
        }
        
        // Afficher tous les mappings trouvés
        System.out.println("Mappings trouvés :");
        for (Map.Entry<String, Method> entry : getMappings.entrySet()) {
            System.out.println(entry.getKey() + " -> " + entry.getValue().getDeclaringClass().getSimpleName() + "." + entry.getValue().getName());
        }
    }

    private void scanControllersInPackage(String packageName) {
        try {
            System.out.println("Configuration du scanner pour " + packageName);
            Reflections reflections = new Reflections(new ConfigurationBuilder()
                .setUrls(ClasspathHelper.forPackage(packageName))
                .setScanners(new TypeAnnotationsScanner(), new MethodAnnotationsScanner()));

            System.out.println("Recherche des classes avec @Controller");
            Set<Class<?>> controllerClasses = reflections.getTypesAnnotatedWith(Controller.class);
            System.out.println("Nombre de contrôleurs trouvés : " + controllerClasses.size());
            
            controllers.addAll(controllerClasses);

            for (Class<?> controllerClass : controllerClasses) {
                System.out.println("Traitement du contrôleur : " + controllerClass.getSimpleName());
                try {
                    Object controllerInstance = controllerClass.getDeclaredConstructor().newInstance();
                    
                    for (Method method : controllerClass.getDeclaredMethods()) {
                        GetMapping mapping = method.getAnnotation(GetMapping.class);
                        if (mapping != null) {
                            String url = mapping.value();
                            if (!url.startsWith("/")) {
                                url = "/" + url;
                            }
                            System.out.println("Mapping trouvé : " + url + " -> " + method.getName());
                            
                            if (getMappings.containsKey(url)) {
                                throw new ServletException("L'URL '" + url + "' est déjà mappée à une autre méthode");
                            }
                            getMappings.put(url, method);
                            controllerInstances.put(method, controllerInstance);
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Erreur lors de l'instanciation du contrôleur " + controllerClass.getSimpleName());
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            System.out.println("Erreur lors du scan du package " + packageName);
            e.printStackTrace();
        }
    }

    private String getRelativeUrl(HttpServletRequest request) {
        String contextPath = request.getContextPath();
        String servletPath = request.getServletPath();
        String requestUri = request.getRequestURI();
        
        String relativeUrl = requestUri.substring(contextPath.length() + servletPath.length());
        return relativeUrl.isEmpty() ? "/" : relativeUrl;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        
        String relativeUrl = getRelativeUrl(request);
        System.out.println("URL demandée : " + relativeUrl);
        
        PrintWriter out = response.getWriter();
        out.println("<html><body>");
        out.println("<h3>Informations de debug :</h3>");
        out.println("URL complète : " + request.getRequestURL() + "<br>");
        out.println("URL relative : " + relativeUrl + "<br>");
        out.println("Nombre de contrôleurs : " + controllers.size() + "<br>");
        out.println("Nombre de mappings : " + getMappings.size() + "<br><br>");
        
        out.println("<h3>Mappings disponibles :</h3>");
        for (Map.Entry<String, Method> entry : getMappings.entrySet()) {
            String url = entry.getKey();
            Method method = entry.getValue();
            Class<?> controllerClass = method.getDeclaringClass();
            out.println(controllerClass.getSimpleName() + "." + method.getName() + " -> " + url + "<br>");
        }
        
        out.println("<h3>Résultat :</h3>");
        Method method = getMappings.get(relativeUrl);
        if (method != null) {
            try {
                Object controllerInstance = controllerInstances.get(method);
                Object result = method.invoke(controllerInstance);
                out.println(result);
            } catch (Exception e) {
                out.println("Erreur lors de l'exécution de la méthode : " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            out.println("Aucune méthode mappée à l'URL : " + relativeUrl);
        }
        
        out.println("</body></html>");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
