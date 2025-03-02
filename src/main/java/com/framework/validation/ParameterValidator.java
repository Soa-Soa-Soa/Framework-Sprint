package com.framework.validation;

import com.framework.annotation.RequestParam;
import java.lang.reflect.Parameter;

/**
 * Valide les paramètres selon leurs annotations
 */
public class ParameterValidator {
    
    /**
     * Valide un paramètre selon ses annotations
     */
    public static ValidationErrors validate(Parameter param, Object value) {
        ValidationErrors errors = new ValidationErrors();
        String paramName = getParamName(param);
        
        // Stocke la valeur soumise
        if (value != null) {
            errors.addValue(paramName, value.toString());
        }
        
        // Validation @Required
        Required required = param.getAnnotation(Required.class);
        if (required != null && value == null) {
            errors.addError(paramName, required.message());
        }
        
        // Si la valeur est null et qu'elle n'est pas requise, on s'arrête là
        if (value == null) {
            return errors;
        }
        
        // Validation @NotBlank pour les String
        if (value instanceof String) {
            NotBlank notBlank = param.getAnnotation(NotBlank.class);
            if (notBlank != null && ((String) value).trim().isEmpty()) {
                errors.addError(paramName, notBlank.message());
            }
        }
        
        // Validation @Range pour les nombres
        Range range = param.getAnnotation(Range.class);
        if (range != null) {
            if (value instanceof Integer) {
                int intValue = (Integer) value;
                if (intValue < range.min() || intValue > range.max()) {
                    String message = range.message()
                        .replace("{min}", String.valueOf(range.min()))
                        .replace("{max}", String.valueOf(range.max()));
                    errors.addError(paramName, message);
                }
            } else if (value instanceof Double) {
                double doubleValue = (Double) value;
                if (doubleValue < range.min() || doubleValue > range.max()) {
                    String message = range.message()
                        .replace("{min}", String.valueOf(range.min()))
                        .replace("{max}", String.valueOf(range.max()));
                    errors.addError(paramName, message);
                }
            }
        }
        
        return errors;
    }
    
    /**
     * Récupère le nom du paramètre depuis l'annotation @RequestParam ou le nom du paramètre
     */
    private static String getParamName(Parameter param) {
        RequestParam requestParam = param.getAnnotation(RequestParam.class);
        if (requestParam != null && !requestParam.name().isEmpty()) {
            return requestParam.name();
        }
        return param.getName();
    }
}
