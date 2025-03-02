package com.framework.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indique qu'une chaîne ne peut pas être vide ou ne contenir que des espaces
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER, ElementType.FIELD})
public @interface NotBlank {
    String message() default "Ce champ ne peut pas être vide";
}
