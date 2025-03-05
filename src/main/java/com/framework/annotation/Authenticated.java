package com.framework.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indique qu'une méthode ou une classe nécessite une authentification.
 * Si appliquée sur une classe, toutes les méthodes de la classe nécessiteront une authentification.
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Authenticated {
    String value() default "";  // Le rôle requis
    String redirectTo() default "/login/form";  // URL de redirection en cas d'absence d'authentification
}
