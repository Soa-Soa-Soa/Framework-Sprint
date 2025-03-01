package com.framework.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation pour spécifier le nom du champ dans la requête HTTP
 * Si le nom n'est pas spécifié, le nom du champ de l'objet sera utilisé
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface RequestField {
    /**
     * Nom du champ dans la requête HTTP
     */
    String value() default "";
    
    /**
     * Indique si le champ est obligatoire
     */
    boolean required() default true;
}
