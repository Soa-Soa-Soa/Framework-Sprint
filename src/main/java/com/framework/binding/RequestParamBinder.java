package com.framework.binding;

import com.framework.annotation.RequestParam;
import com.framework.error.FrameworkException;
import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * Classe utilitaire pour lier les paramètres de requête aux paramètres de méthode
 */
public class RequestParamBinder {
    
    /**
     * Prépare les arguments pour une méthode en fonction des paramètres de la requête
     */
    public static Object[] bindParameters(Method method, HttpServletRequest request) throws FrameworkException {
        Parameter[] parameters = method.getParameters();
        Object[] args = new Object[parameters.length];
        
        for (int i = 0; i < parameters.length; i++) {
            Parameter param = parameters[i];
            RequestParam annotation = param.getAnnotation(RequestParam.class);
            Class<?> paramType = param.getType();
            
            // Si c'est un type primitif ou String, il doit avoir @RequestParam
            if (isPrimitiveOrString(paramType)) {
                if (annotation == null) {
                    throw FrameworkException.missingAnnotation(param.getName(), method.getName());
                }
                // Paramètre simple avec @RequestParam
                String paramName = getParameterName(param);
                String paramValue = request.getParameter(paramName);
                boolean isRequired = annotation.required();
                
                if (paramValue == null && isRequired) {
                    throw FrameworkException.missingRequiredParameter(paramName, method.getName());
                }
                
                args[i] = convertValue(paramValue, paramType, paramName);
            } else {
                // Objet complexe
                args[i] = ObjectBinder.bindObject(paramType, request);
            }
        }
        
        return args;
    }
    
    /**
     * Obtient le nom du paramètre, soit depuis l'annotation @RequestParam, soit depuis le nom du paramètre
     */
    private static String getParameterName(Parameter param) {
        RequestParam annotation = param.getAnnotation(RequestParam.class);
        if (annotation != null && !annotation.value().isEmpty()) {
            return annotation.value();
        }
        return param.getName();
    }
    
    /**
     * Vérifie si le type est un primitif, String, Number ou Boolean
     */
    private static boolean isPrimitiveOrString(Class<?> type) {
        return type.isPrimitive() || 
               type == String.class || 
               Number.class.isAssignableFrom(type) || 
               type == Boolean.class;
    }
    
    /**
     * Convertit une valeur String en type cible
     */
    private static Object convertValue(String value, Class<?> targetType, String paramName) throws FrameworkException {
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
            throw FrameworkException.unsupportedParameterType(paramName, targetType.getSimpleName());
        } catch (NumberFormatException e) {
            throw FrameworkException.parameterConversionError(paramName, targetType.getSimpleName(), value);
        }
    }
}
