package com.framework.session;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Classe représentant une session utilisateur
 */
public class Session {
    private final String id;
    private final Map<String, Object> attributes;
    private long lastAccessTime;
    private static final int MAX_INACTIVE_INTERVAL = 1800; // 30 minutes en secondes
    
    public Session() {
        this.id = UUID.randomUUID().toString();
        this.attributes = new HashMap<>();
        this.lastAccessTime = System.currentTimeMillis();
    }
    
    /**
     * Met à jour le temps du dernier accès
     */
    public void touch() {
        this.lastAccessTime = System.currentTimeMillis();
    }
    
    /**
     * Vérifie si la session est expirée
     */
    public boolean isExpired() {
        long currentTime = System.currentTimeMillis();
        return (currentTime - lastAccessTime) / 1000 > MAX_INACTIVE_INTERVAL;
    }
    
    /**
     * Récupère un attribut de la session
     */
    public Object getAttribute(String name) {
        touch();
        return attributes.get(name);
    }
    
    /**
     * Définit un attribut dans la session
     */
    public void setAttribute(String name, Object value) {
        touch();
        attributes.put(name, value);
    }
    
    /**
     * Supprime un attribut de la session
     */
    public void removeAttribute(String name) {
        touch();
        attributes.remove(name);
    }
    
    /**
     * Invalide la session en supprimant tous les attributs
     */
    public void invalidate() {
        attributes.clear();
    }
    
    /**
     * Récupère l'identifiant unique de la session
     */
    public String getId() {
        return id;
    }
    
    /**
     * Récupère le temps du dernier accès
     */
    public long getLastAccessTime() {
        return lastAccessTime;
    }
}
