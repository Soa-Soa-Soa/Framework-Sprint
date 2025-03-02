package com.framework.http;

/**
 * Énumération des verbes HTTP supportés par le framework
 */
public enum HttpVerb {
    GET,
    POST,
    PUT,
    DELETE,
    PATCH;
    
    /**
     * Convertit une chaîne en HttpVerb
     * @param method La méthode HTTP en string
     * @return Le verbe HTTP correspondant
     */
    public static HttpVerb fromString(String method) {
        return valueOf(method.toUpperCase());
    }
}
