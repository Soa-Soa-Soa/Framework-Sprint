package com.framework.error;

/**
 * Exception personnalisée pour le framework
 * Permet de gérer les différentes erreurs de manière uniforme
 */
public class FrameworkException extends Exception {
    private final int statusCode;
    
    public FrameworkException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }
    
    public int getStatusCode() {
        return statusCode;
    }
    
    /**
     * Package des contrôleurs inexistant
     */
    public static FrameworkException controllerPackageNotFound(String packageName) {
        return new FrameworkException(
            "Le package des contrôleurs '" + packageName + "' n'existe pas",
            500
        );
    }
    
    /**
     * Package des contrôleurs vide
     */
    public static FrameworkException emptyControllerPackage(String packageName) {
        return new FrameworkException(
            "Le package des contrôleurs '" + packageName + "' est vide",
            500
        );
    }
    
    /**
     * Mappage d'URL similaire
     */
    public static FrameworkException duplicateMapping(String url, String existingController, String newController) {
        return new FrameworkException(
            "L'URL '" + url + "' est déjà mappée dans " + existingController + 
            ". Impossible de la mapper à nouveau dans " + newController,
            500
        );
    }
    
    /**
     * URL non trouvée
     */
    public static FrameworkException urlNotFound(String url) {
        return new FrameworkException(
            "L'URL '" + url + "' n'existe pas",
            404
        );
    }
    
    /**
     * Type de retour de méthode non supporté
     */
    public static FrameworkException unsupportedReturnType(String methodName, String returnType) {
        return new FrameworkException(
            "La méthode '" + methodName + "' a un type de retour non supporté : " + returnType + 
            ". Types supportés : String, ModelView",
            500
        );
    }
}
