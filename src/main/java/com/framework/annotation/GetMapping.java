package com.framework.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation pour mapper une méthode à une URL HTTP GET
 * 
 * Utilisation :
 * @GetMapping("/users")
 * public String getUsers() { ... }
 * 
 * Cette annotation permet au FrontController de :
 * 1. Identifier les méthodes qui doivent répondre aux requêtes HTTP
 * 2. Associer chaque méthode à une URL spécifique
 * 3. Invoquer automatiquement la bonne méthode selon l'URL demandée
 */
@Retention(RetentionPolicy.RUNTIME) // L'annotation doit être accessible à l'exécution
@Target(ElementType.METHOD)         // L'annotation ne peut être utilisée que sur des méthodes
public @interface GetMapping {
    /**
     * L'URL à laquelle la méthode doit répondre
     * 
     * Exemples :
     * - "/"      -> page d'accueil
     * - "/users" -> liste des utilisateurs
     * - "/hello" -> page de salutation
     * 
     * Note : Si l'URL ne commence pas par "/", il sera ajouté automatiquement
     * 
     * @return L'URL associée à la méthode
     */
    String value();
}
