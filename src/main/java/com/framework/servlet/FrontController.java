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

/**
 * FrontController : Point d'entrée unique de l'application
 * Implémente le pattern Front Controller qui centralise le traitement des requêtes
 * 
 * Responsabilités :
 * 1. Scanner et instancier les contrôleurs (pattern Singleton)
 * 2. Gérer le mapping des URLs vers les méthodes
 * 3. Invoquer les méthodes appropriées et retourner leur résultat
 */
public class FrontController extends HttpServlet {
    // Map qui stocke une seule instance de chaque contrôleur (pattern Singleton)
    // Clé : Classe du contrôleur, Valeur : Instance unique du contrôleur
    private static final Map<Class<?>, Object> controllerInstances = new HashMap<>();

    // Map qui associe les URLs aux méthodes des contrôleurs
    // Clé : URL (ex: "/users"), Valeur : Méthode à exécuter
    private static final Map<String, Method> getMappings = new HashMap<>();

    /**
     * Méthode d'initialisation appelée une seule fois au démarrage du servlet
     * Configure le FrontController en scannant les contrôleurs
     */
    @Override
    public void init() throws ServletException {
        System.out.println("FrontController - Initialisation...");
        
        // Récupérer le package à scanner depuis web.xml
        String packageName = getInitParameter("controllerPackage");
        if (packageName == null || packageName.trim().isEmpty()) {
            throw new ServletException("Le package des contrôleurs n'est pas spécifié dans web.xml");
        }
        
        // Lancer le scan des contrôleurs
        scanControllers(packageName.trim());
    }

    /**
     * Scanne un package pour trouver les classes annotées avec @Controller
     * Utilise le ClassLoader pour parcourir les fichiers .class
     * 
     * @param basePackage Nom du package à scanner (ex: "com.framework.controller")
     */
    private void scanControllers(String basePackage) {
        try {
            // Convertir le nom du package en chemin de fichier
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            String path = basePackage.replace('.', '/');
            
            // Récupérer toutes les ressources correspondant au package
            Enumeration<java.net.URL> resources = classLoader.getResources(path);
            while (resources.hasMoreElements()) {
                java.net.URL resource = resources.nextElement();
                java.io.File directory = new java.io.File(resource.getFile());
                
                if (directory.exists()) {
                    // Scanner récursivement le répertoire
                    scanDirectory(directory, basePackage);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Scanne récursivement un répertoire pour trouver les fichiers .class
     * 
     * @param directory Répertoire à scanner
     * @param basePackage Nom du package correspondant au répertoire
     */
    private void scanDirectory(java.io.File directory, String basePackage) {
        java.io.File[] files = directory.listFiles();
        if (files != null) {
            for (java.io.File file : files) {
                if (file.isDirectory()) {
                    // Si c'est un sous-répertoire, scanner récursivement
                    scanDirectory(file, basePackage + "." + file.getName());
                } else if (file.getName().endsWith(".class")) {
                    // Si c'est un fichier .class, le traiter
                    String className = basePackage + '.' + file.getName().substring(0, file.getName().length() - 6);
                    processClass(className);
                }
            }
        }
    }

    /**
     * Traite une classe trouvée lors du scan
     * Si la classe est annotée avec @Controller :
     * 1. Crée une instance unique (Singleton)
     * 2. Scanne ses méthodes pour trouver les @GetMapping
     * 
     * @param className Nom complet de la classe (avec package)
     */
    private void processClass(String className) {
        try {
            Class<?> clazz = Class.forName(className);
            if (clazz.isAnnotationPresent(Controller.class)) {
                System.out.println("Contrôleur trouvé : " + clazz.getName());
                
                // Créer une seule instance du contrôleur (Singleton)
                Object controllerInstance = clazz.getDeclaredConstructor().newInstance();
                controllerInstances.put(clazz, controllerInstance);
                
                // Scanner les méthodes pour @GetMapping
                for (Method method : clazz.getDeclaredMethods()) {
                    GetMapping mapping = method.getAnnotation(GetMapping.class);
                    if (mapping != null) {
                        String url = mapping.value();
                        if (!url.startsWith("/")) {
                            url = "/" + url;
                        }
                        
                        // Vérifier qu'il n'y a pas de double mapping
                        if (getMappings.containsKey(url)) {
                            throw new ServletException("L'URL '" + url + "' est déjà mappée à une autre méthode");
                        }
                        
                        // Enregistrer le mapping URL -> méthode
                        getMappings.put(url, method);
                        System.out.println("Mapping trouvé : " + url + " -> " + clazz.getSimpleName() + "." + method.getName());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Extrait l'URL relative de la requête
     * Exemple : Pour http://localhost:8080/sprint/app/users
     * Retourne : /users
     */
    private String getRelativeUrl(HttpServletRequest request) {
        String contextPath = request.getContextPath();    // /sprint
        String servletPath = request.getServletPath();    // /app
        String requestUri = request.getRequestURI();      // /sprint/app/users
        
        // Enlever le contextPath et le servletPath
        String relativeUrl = requestUri.substring(contextPath.length() + servletPath.length());
        return relativeUrl.isEmpty() ? "/" : relativeUrl;
    }

    /**
     * Traite les requêtes GET
     * 1. Extrait l'URL relative
     * 2. Trouve la méthode correspondante
     * 3. Invoque la méthode et retourne son résultat
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        
        String relativeUrl = getRelativeUrl(request);
        PrintWriter out = response.getWriter();
        
        // Chercher une méthode correspondant à l'URL
        Method method = getMappings.get(relativeUrl);
        if (method != null) {
            try {
                // Obtenir l'instance Singleton du contrôleur
                Object controllerInstance = controllerInstances.get(method.getDeclaringClass());
                
                // Invoquer la méthode et récupérer le résultat
                Object result = method.invoke(controllerInstance);
                
                // Afficher le résultat s'il n'est pas null
                if (result != null) {
                    out.println(result.toString());
                }
            } catch (Exception e) {
                throw new ServletException("Erreur lors de l'exécution de la méthode", e);
            }
        } else {
            // URL non trouvée -> erreur 404
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            out.println("URL non trouvée : " + relativeUrl);
        }
    }

    /**
     * Les requêtes POST sont traitées comme des GET pour l'instant
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
