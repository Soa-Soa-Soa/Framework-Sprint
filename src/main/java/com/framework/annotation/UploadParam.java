package com.framework.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indique qu'un paramètre doit recevoir un fichier uploadé
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface UploadParam {
    /**
     * Nom du paramètre dans le formulaire HTML
     */
    String value();
}
