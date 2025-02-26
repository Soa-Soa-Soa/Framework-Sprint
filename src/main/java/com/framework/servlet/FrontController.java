package com.framework.servlet;

import com.framework.annotation.Controller;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class FrontController extends HttpServlet {
    private String packageName;
    private List<Class<?>> controllers = new ArrayList<>();
    private boolean scanned = false;
    
    @Override
    public void init() throws ServletException {
        super.init();
        // Récupérer le nom du package depuis web.xml
        packageName = getServletConfig().getInitParameter("controllerPackage");
        if (packageName == null || packageName.trim().isEmpty()) {
            throw new ServletException("Le package des contrôleurs n'est pas spécifié dans web.xml");
        }
    }
    
    private void scanControllers() throws ServletException {
        try {
            // Convertir le nom du package en chemin
            String path = packageName.replace('.', '/');
            URL resource = Thread.currentThread().getContextClassLoader().getResource(path);
            
            if (resource == null) {
                throw new ServletException("Package " + packageName + " introuvable");
            }
            
            File directory = new File(resource.getFile());
            if (directory.exists()) {
                // Parcourir tous les fichiers .class du package
                File[] files = directory.listFiles((dir, name) -> name.endsWith(".class"));
                if (files != null) {
                    for (File file : files) {
                        String className = packageName + '.' + file.getName().substring(0, file.getName().length() - 6);
                        Class<?> clazz = Class.forName(className);
                        
                        // Vérifier si la classe a l'annotation @Controller
                        if (clazz.isAnnotationPresent(Controller.class)) {
                            controllers.add(clazz);
                        }
                    }
                }
            }
            scanned = true;
        } catch (Exception e) {
            throw new ServletException("Erreur lors du scan des contrôleurs", e);
        }
    }
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        
        // Scanner les contrôleurs si ce n'est pas déjà fait
        if (!scanned) {
            scanControllers();
        }
        
        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>FrontController</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Current URL: " + request.getRequestURL() + "</h1>");
            
            // Afficher la liste des contrôleurs
            out.println("<h2>Liste des contrôleurs trouvés :</h2>");
            out.println("<ul>");
            for (Class<?> controller : controllers) {
                out.println("<li>" + controller.getSimpleName() + "</li>");
            }
            out.println("</ul>");
            
            out.println("</body>");
            out.println("</html>");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
}
