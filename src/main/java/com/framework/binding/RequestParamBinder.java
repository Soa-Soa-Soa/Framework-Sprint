package com.framework.binding;

import com.framework.annotation.*;
import com.framework.error.FrameworkException;
import com.framework.upload.WinterPart;
import com.framework.validation.ParameterValidator;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * Classe utilitaire pour le binding des paramètres de requête HTTP
 * vers les paramètres des méthodes de contrôleur
 */
public class RequestParamBinder {
    
    /**
     * Prépare les arguments pour une méthode de contrôleur en fonction des paramètres de la requête
     * 
     * Supporte :
     * - Les paramètres simples avec @RequestParam
     * - Les fichiers uploadés avec @UploadParam
     * - Les objets complexes avec binding automatique
     * - La validation des paramètres (@Required, @NotBlank, @Range)
     * 
     * @param method Méthode de contrôleur à invoquer
     * @param request Requête HTTP contenant les paramètres
     * @return Tableau d'arguments à passer à la méthode
     * @throws Exception Si une erreur survient pendant le binding ou la validation
     */
    public static Object[] bindParameters(Method method, HttpServletRequest request) throws Exception {
        Parameter[] parameters = method.getParameters();
        Object[] args = new Object[parameters.length];
        
        for (int i = 0; i < parameters.length; i++) {
            Parameter param = parameters[i];
            Object value = null;
            
            if (param.isAnnotationPresent(UploadParam.class)) {
                value = bindUploadParam(param, request);
            } else {
                RequestParam annotation = param.getAnnotation(RequestParam.class);
                Class<?> paramType = param.getType();
                
                // Si le paramètre n'a pas d'annotation, on utilise son nom
                String paramName = getParamName(param, annotation);
                String paramValue = request.getParameter(paramName);
                
                // Conversion de la valeur si présente
                if (paramValue != null) {
                    value = ObjectBinder.convertValue(paramValue, paramType, paramName);
                }
            }
            
            // Validation du paramètre
            ParameterValidator.validate(param, value);
            args[i] = value;
        }
        
        return args;
    }
    
    /**
     * Gère le binding d'un fichier uploadé vers un objet WinterPart
     * 
     * @param param Paramètre de la méthode annoté avec @UploadParam
     * @param request Requête HTTP contenant le fichier
     * @return Instance de WinterPart configurée avec le fichier
     * @throws IOException En cas d'erreur d'accès au fichier
     * @throws ServletException En cas d'erreur lors de la récupération du Part
     * @throws FrameworkException Si le fichier est requis mais absent
     */
    private static WinterPart bindUploadParam(Parameter param, HttpServletRequest request) 
            throws IOException, ServletException, FrameworkException {
        UploadParam annotation = param.getAnnotation(UploadParam.class);
        String paramName = annotation.value();
        
        // Récupère le fichier uploadé
        Part part = request.getPart(paramName);
        if (part == null) {
            throw FrameworkException.missingUploadParam(paramName);
        }
        
        // Crée le répertoire d'upload s'il n'existe pas
        String uploadDir = request.getServletContext().getRealPath("") + "/uploads";
        
        // Crée et configure l'objet WinterPart
        WinterPart winterPart = new WinterPart();
        winterPart.setPart(part);
        winterPart.setUploadDirectory(uploadDir);
        
        return winterPart;
    }
    
    /**
     * Obtient le nom du paramètre à partir de l'annotation ou du nom du paramètre
     * 
     * @param param Paramètre de la méthode
     * @param annotation Annotation @RequestParam si présente
     * @return Nom du paramètre à utiliser pour le binding
     */
    private static String getParamName(Parameter param, RequestParam annotation) {
        if (annotation != null && !annotation.value().isEmpty()) {
            return annotation.value();
        }
        return param.getName();
    }
}
