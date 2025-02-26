package com.framework.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation pour marquer une classe comme étant un contrôleur
 * 
 * Un contrôleur est responsable de :
 * 1. Recevoir les requêtes HTTP
 * 2. Traiter ces requêtes
 * 3. Retourner une réponse appropriée
 * 
 * Utilisation :
 * @Controller
 * public class UserController {
 *     @GetMapping("/users")
 *     public String getUsers() { ... }
 * }
 * 
 * Note : Les contrôleurs sont gérés comme des Singletons par le framework
 * Une seule instance est créée et réutilisée pour toutes les requêtes
 */
@Retention(RetentionPolicy.RUNTIME) // L'annotation doit être accessible à l'exécution
@Target(ElementType.TYPE)           // L'annotation ne peut être utilisée que sur des classes
public @interface Controller {
}
