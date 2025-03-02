package com.framework.binding;

import com.framework.annotation.RequestField;
import com.framework.error.FrameworkException;
import jakarta.servlet.http.HttpServletRequest;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Classe utilitaire pour lier les paramètres de requête aux champs d'un objet
 */
public class ObjectBinder {
    
    /**
     * Crée une instance d'une classe et remplit ses champs avec les paramètres de la requête
     * 
     * @param clazz Classe de l'objet à créer
     * @param request Requête HTTP
     * @return Instance de l'objet avec ses champs remplis
     * @throws FrameworkException Si un champ requis est manquant ou si la conversion échoue
     */
    public static <T> T bindObject(Class<T> clazz, HttpServletRequest request) throws FrameworkException {
        try {
            T instance = clazz.getDeclaredConstructor().newInstance();
            
            // Récupérer tous les champs de la classe
            Map<String, String> fieldMappings = new HashMap<>();
            for (Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true);
                String requestParamName = getRequestParamName(field);
                fieldMappings.put(requestParamName, field.getName());
            }
            
            // Remplir les champs avec les valeurs de la requête
            for (Map.Entry<String, String> entry : fieldMappings.entrySet()) {
                String requestParamName = entry.getKey();
                String fieldName = entry.getValue();
                Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                
                String paramValue = request.getParameter(requestParamName);
                RequestField annotation = field.getAnnotation(RequestField.class);
                boolean isRequired = annotation != null ? annotation.required() : true;
                
                if (paramValue == null && isRequired) {
                    throw FrameworkException.missingRequiredField(requestParamName, clazz.getSimpleName());
                }
                
                if (paramValue != null) {
                    Object convertedValue = convertValue(paramValue, field.getType(), requestParamName);
                    field.set(instance, convertedValue);
                }
            }
            
            return instance;
        } catch (FrameworkException e) {
            throw e;
        } catch (Exception e) {
            throw FrameworkException.objectBindingError(clazz.getSimpleName(), e.getMessage());
        }
    }
    
    /**
     * Obtient le nom du paramètre de requête pour un champ
     */
    private static String getRequestParamName(Field field) {
        RequestField annotation = field.getAnnotation(RequestField.class);
        if (annotation != null && !annotation.value().isEmpty()) {
            return annotation.value();
        }
        return field.getName();
    }
    
    /**
     * Convertit une valeur String en type cible
     */
    static Object convertValue(String value, Class<?> targetType, String fieldName) throws FrameworkException {
        if (value == null) return null;
        
        try {
            if (targetType == String.class) {
                return value;
            } else if (targetType == int.class || targetType == Integer.class) {
                return Integer.parseInt(value);
            } else if (targetType == long.class || targetType == Long.class) {
                return Long.parseLong(value);
            } else if (targetType == double.class || targetType == Double.class) {
                return Double.parseDouble(value);
            } else if (targetType == boolean.class || targetType == Boolean.class) {
                return Boolean.parseBoolean(value);
            } else if (targetType == float.class || targetType == Float.class) {
                return Float.parseFloat(value);
            }
            throw FrameworkException.unsupportedFieldType(fieldName, targetType.getSimpleName());
        } catch (NumberFormatException e) {
            throw FrameworkException.fieldConversionError(fieldName, targetType.getSimpleName(), value);
        }
    }
}
