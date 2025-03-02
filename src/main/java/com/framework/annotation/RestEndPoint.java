package com.framework.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation pour marquer une méthode comme point d'entrée REST
 * La méthode retournera automatiquement une réponse JSON
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RestEndPoint {
    /**
     * URL du point d'entrée REST
     */
    String value() default "";
}
