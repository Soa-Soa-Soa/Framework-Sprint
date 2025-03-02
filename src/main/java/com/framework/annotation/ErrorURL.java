package com.framework.annotation;

import java.lang.annotation.*;

/**
 * Spécifie l'URL de redirection en cas d'erreur de validation
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ErrorURL {
    String value();
}
