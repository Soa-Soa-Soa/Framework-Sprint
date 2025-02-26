package com.framework.modelview;

import java.util.HashMap;

/**
 * Classe ModelView qui gère la vue et les données à afficher
 * 
 * Cette classe permet de :
 * 1. Spécifier la vue (page JSP) à afficher
 * 2. Passer des données du contrôleur à la vue
 * 
 * Exemple d'utilisation dans un contrôleur :
 * @GetMapping("/users")
 * public ModelView getUsers() {
 *     ModelView mv = new ModelView("users.jsp");
 *     mv.addItem("users", usersList);
 *     mv.addItem("title", "Liste des utilisateurs");
 *     return mv;
 * }
 */
public class ModelView {
    
    /**
     * URL de la vue (page JSP) à afficher
     * Exemple : "users.jsp", "home.jsp"
     */
    private String url;
    
    /**
     * Map contenant les données à passer à la vue
     * Clé : nom de l'attribut dans la JSP
     * Valeur : objet à passer (peut être de n'importe quel type)
     */
    private HashMap<String, Object> data;
    
    /**
     * Constructeur qui initialise le ModelView avec l'URL de la vue
     * 
     * @param url URL de la vue à afficher
     */
    public ModelView(String url) {
        this.url = url;
        this.data = new HashMap<>();
    }
    
    /**
     * Ajoute une donnée à passer à la vue
     * 
     * @param name Nom de l'attribut dans la JSP
     * @param value Valeur de l'attribut
     */
    public void addItem(String name, Object value) {
        this.data.put(name, value);
    }
    
    /**
     * Récupère l'URL de la vue
     * 
     * @return URL de la vue
     */
    public String getUrl() {
        return url;
    }
    
    /**
     * Modifie l'URL de la vue
     * 
     * @param url Nouvelle URL de la vue
     */
    public void setUrl(String url) {
        this.url = url;
    }
    
    /**
     * Récupère les données à passer à la vue
     * 
     * @return Map des données
     */
    public HashMap<String, Object> getData() {
        return data;
    }
    
    /**
     * Modifie les données à passer à la vue
     * 
     * @param data Nouvelles données
     */
    public void setData(HashMap<String, Object> data) {
        this.data = data;
    }
}
