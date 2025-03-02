package com.framework.validation;

import java.util.HashMap;
import java.util.Map;

/**
 * Stocke les erreurs de validation et les valeurs soumises
 * pour les réafficher dans le formulaire
 */
public class ValidationErrors {
    private final Map<String, String> errors = new HashMap<>();
    private final Map<String, String> values = new HashMap<>();
    
    public void addError(String field, String message) {
        errors.put(field, message);
    }
    
    public void addValue(String field, String value) {
        values.put(field, value);
    }
    
    public Map<String, String> getErrors() {
        return errors;
    }
    
    public Map<String, String> getValues() {
        return values;
    }
    
    public boolean hasErrors() {
        return !errors.isEmpty();
    }
    
    public void merge(ValidationErrors other) {
        if (other != null) {
            errors.putAll(other.getErrors());
            values.putAll(other.getValues());
        }
    }
}
