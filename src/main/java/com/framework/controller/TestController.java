package com.framework.controller;

import com.framework.annotation.Controller;
import com.framework.annotation.GetMapping;
import com.framework.annotation.RequestParam;
import com.framework.model.User;
import com.framework.modelview.ModelView;

/**
 * Contrôleur de test pour démontrer l'utilisation du framework
 * 
 * Ce contrôleur fournit plusieurs endpoints :
 * 1. "/" -> page d'accueil (texte simple)
 * 2. "/test" -> page de test (texte simple)
 * 3. "/hello" -> message de salutation (texte simple)
 * 4. "/greet" -> message de salutation personnalisé
 * 5. "/calculate" -> calcul simple
 * 6. "/view" -> page JSP avec données
 * 7. "/user/register" -> test du binding d'objets
 * 8. "/user/update" -> test du binding mixte (objet + paramètres simples)
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
     * URL : http://localhost:8080/sprint/app/hello?name=John
     * 
     * @param name Nom de la personne à saluer
     * @return Message "Hello John!"
     */
    @GetMapping("/hello")
    public String hello(@RequestParam(name = "name") String name) {
        return "Hello " + name + "!";
    }
    
    /**
     * Message de salutation personnalisé
     * URL : http://localhost:8080/sprint/app/greet?firstName=John&lastName=Doe
     * 
     * @param prenom Prénom de la personne à saluer
     * @param nom Nom de la personne à saluer (facultatif)
     * @return Message "Bonjour John Doe!"
     */
    @GetMapping("/greet")
    public String greet(
        @RequestParam(name = "firstName") String prenom,
        @RequestParam(value = "lastName", required = false, name = "lastName") String nom
    ) {
        if (nom != null) {
            return "Bonjour " + prenom + " " + nom + "!";
        }
        return "Bonjour " + prenom + "!";
    }
    
    /**
     * Calcul simple
     * URL : http://localhost:8080/sprint/app/calculate?a=2&b=3&operation=add
     * 
     * @param a Premier nombre
     * @param b Deuxième nombre
     * @param operation Opération à effectuer (facultatif, par défaut : add)
     * @return Résultat du calcul
     */
    @GetMapping("/calculate")
    public String calculate(
        @RequestParam(name = "a") int a,
        @RequestParam(name = "b") int b,
        @RequestParam(value = "operation", required = false, name = "operation") String operation
    ) {
        if (operation == null) operation = "add";
        
        switch (operation.toLowerCase()) {
            case "add":
                return a + " + " + b + " = " + (a + b);
            case "subtract":
                return a + " - " + b + " = " + (a - b);
            case "multiply":
                return a + " × " + b + " = " + (a * b);
            case "divide":
                if (b == 0) return "Division par zéro impossible";
                return a + " ÷ " + b + " = " + (a / b);
            default:
                return "Opération non supportée : " + operation;
        }
    }
    
    /**
     * Exemple d'utilisation de ModelView
     * URL : http://localhost:8080/sprint/app/view?title=Page+de+test&message=Ceci+est+un+message
     * 
     * @param title Titre de la page (facultatif)
     * @param message Message de la page (facultatif)
     * @return ModelView avec données pour la vue test.jsp
     */
    @GetMapping("/view")
    public ModelView testView(
        @RequestParam(required = false, name = "title") String title,
        @RequestParam(required = false, name = "message") String message
    ) {
        ModelView mv = new ModelView("test.jsp");
        mv.addItem("title", title != null ? title : "Page de test avec ModelView");
        mv.addItem("message", message != null ? message : "Cette page utilise une JSP avec des données !");
        return mv;
    }

    /**
     * Test du binding d'objets
     * URL : http://localhost:8080/sprint/app/user/register?firstName=John&lastName=Doe&age=25&email=john@example.com
     */
    @GetMapping("/user/register")
    public String registerUser(User user) {
        return "Utilisateur enregistré : " + user.toString();
    }

    /**
     * Test du binding mixte (objet + paramètres simples)
     * URL : http://localhost:8080/sprint/app/user/update?firstName=John&lastName=Doe&role=admin
     */
    @GetMapping("/user/update")
    public String updateUser(User user, @RequestParam(name = "role") String role) {
        return "Mise à jour de l'utilisateur : " + user.toString() + " avec le rôle : " + role;
    }
}
