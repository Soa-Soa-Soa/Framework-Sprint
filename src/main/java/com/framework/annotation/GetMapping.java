package com.framework.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation pour mapper une méthode à une URL spécifique pour les requêtes GET.
 * Exemple d'utilisation :
 * @GetMapping("/users")
 * public String getUsers() { ... }
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface GetMapping {
    /**
     * L'URL à laquelle la méthode doit répondre.
     * Doit commencer par "/".
     */
    String value();
}
