package com.framework.validation;

import com.framework.error.FrameworkException;
import java.lang.reflect.Parameter;

/**
 * Validateur de paramètres de requête
 * Vérifie que les paramètres respectent les contraintes définies par les annotations
 */
public class ParameterValidator {
    
    /**
     * Valide un paramètre selon ses annotations
     * 
     * @param param Paramètre à valider
     * @param value Valeur du paramètre
     * @throws FrameworkException Si la validation échoue
     */
    public static void validate(Parameter param, Object value) throws FrameworkException {
        // Validation @Required
        if (param.isAnnotationPresent(Required.class)) {
            Required required = param.getAnnotation(Required.class);
            if (value == null) {
                throw new FrameworkException(required.message(), 400);
            }
        }
        
        // Si la valeur est null et pas @Required, on skip les autres validations
        if (value == null) {
            return;
        }
        
        // Validation @NotBlank pour les String
        if (param.isAnnotationPresent(NotBlank.class) && value instanceof String) {
            NotBlank notBlank = param.getAnnotation(NotBlank.class);
            String strValue = (String) value;
            if (strValue.trim().isEmpty()) {
                throw new FrameworkException(notBlank.message(), 400);
            }
        }
        
        // Validation @Range pour les nombres
        if (param.isAnnotationPresent(Range.class) && value instanceof Number) {
            Range range = param.getAnnotation(Range.class);
            long numValue = ((Number) value).longValue();
            
            if (numValue < range.min() || numValue > range.max()) {
                String message = range.message()
                    .replace("{min}", String.valueOf(range.min()))
                    .replace("{max}", String.valueOf(range.max()));
                throw new FrameworkException(message, 400);
            }
        }
    }
}
