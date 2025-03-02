package com.framework.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Vérifie qu'une valeur numérique est comprise dans un intervalle
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER, ElementType.FIELD})
public @interface Range {
    long min() default Long.MIN_VALUE;
    long max() default Long.MAX_VALUE;
    String message() default "La valeur doit être comprise entre {min} et {max}";
}
