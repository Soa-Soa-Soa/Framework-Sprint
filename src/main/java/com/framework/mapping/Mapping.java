package com.framework.mapping;

import com.framework.http.HttpVerb;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Classe gérant les mappings entre URLs, verbes HTTP et méthodes de contrôleur
 */
public class Mapping {
    private final String url;
    private final Map<HttpVerb, Method> methodMappings;
    private final Map<HttpVerb, Class<?>> controllerMappings;

    public Mapping(String url) {
        this.url = url;
        this.methodMappings = new HashMap<>();
        this.controllerMappings = new HashMap<>();
    }

    /**
     * Ajoute un mapping pour un verbe HTTP spécifique
     * @param verb Le verbe HTTP
     * @param method La méthode du contrôleur
     * @param controllerClass La classe du contrôleur
     */
    public void addMethodMapping(HttpVerb verb, Method method, Class<?> controllerClass) {
        methodMappings.put(verb, method);
        controllerMappings.put(verb, controllerClass);
    }

    /**
     * Récupère la méthode associée à un verbe HTTP
     * @param verb Le verbe HTTP
     * @return La méthode correspondante ou null si non trouvée
     */
    public Method getMethod(HttpVerb verb) {
        return methodMappings.get(verb);
    }

    /**
     * Récupère la classe du contrôleur associée à un verbe HTTP
     * @param verb Le verbe HTTP
     * @return La classe du contrôleur ou null si non trouvée
     */
    public Class<?> getControllerClass(HttpVerb verb) {
        return controllerMappings.get(verb);
    }

    /**
     * Vérifie si un verbe HTTP est supporté pour cette URL
     * @param verb Le verbe HTTP
     * @return true si le verbe est supporté
     */
    public boolean supportsVerb(HttpVerb verb) {
        return methodMappings.containsKey(verb);
    }

    public String getUrl() {
        return url;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Mapping mapping = (Mapping) o;
        return Objects.equals(url, mapping.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url);
    }
}
