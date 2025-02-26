package com.framework.controller;

import com.framework.annotation.Controller;
import com.framework.annotation.GetMapping;
import com.framework.modelview.ModelView;

/**
 * Contrôleur de test pour démontrer l'utilisation du framework
 * 
 * Ce contrôleur fournit quatre endpoints :
 * 1. "/" -> page d'accueil (texte simple)
 * 2. "/test" -> page de test (texte simple)
 * 3. "/hello" -> message de salutation (texte simple)
 * 4. "/view" -> page JSP avec données
 */
@Controller
public class TestController {
    
    /**
     * Page d'accueil de l'application
     * URL : http://localhost:8080/sprint/app/
     * 
     * @return Message de bienvenue
     */
    @GetMapping("/")
    public String index() {
        return "Page d'accueil";
    }
    
    /**
     * Page de test
     * URL : http://localhost:8080/sprint/app/test
     * 
     * @return Message de test
     */
    @GetMapping("/test")
    public String test() {
        return "Page de test";
    }
    
    /**
     * Message de salutation
     * URL : http://localhost:8080/sprint/app/hello
     * 
     * @return Message "Hello World!"
     */
    @GetMapping("/hello")
    public String hello() {
        return "Hello World!";
    }
    
    /**
     * Exemple d'utilisation de ModelView
     * URL : http://localhost:8080/sprint/app/view
     * 
     * @return ModelView avec données pour la vue test.jsp
     */
    @GetMapping("/view")
    public ModelView testView() {
        ModelView mv = new ModelView("test.jsp");
        mv.addItem("title", "Page de test avec ModelView");
        mv.addItem("message", "Cette page utilise une JSP avec des données !");
        return mv;
    }
}
