package com.framework.controller;

import com.framework.annotation.Controller;
import com.framework.annotation.GetMapping;

/**
 * Contrôleur de test pour démontrer l'utilisation du framework
 * 
 * Ce contrôleur fournit trois endpoints :
 * 1. "/" -> page d'accueil
 * 2. "/test" -> page de test
 * 3. "/hello" -> message de salutation
 * 
 * Chaque méthode est mappée à une URL spécifique via @GetMapping
 * et retourne une chaîne qui sera affichée dans le navigateur
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
}
