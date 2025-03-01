package com.framework.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation pour lier un paramètre de méthode à un paramètre de requête HTTP
 * Si le nom du paramètre n'est pas spécifié, le nom du paramètre de la méthode sera utilisé
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface RequestParam {
    /**
     * Nom du paramètre dans la requête HTTP
     * Si non spécifié, le nom du paramètre de la méthode sera utilisé
     */
    String value() default "";
    
    /**
     * Indique si le paramètre est obligatoire
     */
    boolean required() default true;
}
