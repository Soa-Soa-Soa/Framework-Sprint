package com.framework.modelview;

import java.util.HashMap;
import java.util.Map;

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
    private final String view;
    
    /**
     * Map contenant les données à passer à la vue
     * Clé : nom de l'attribut dans la JSP
     * Valeur : objet à passer (peut être de n'importe quel type)
     */
    private final Map<String, Object> model;
    
    /**
     * Constructeur qui initialise le ModelView avec l'URL de la vue
     * 
     * @param view URL de la vue à afficher
     */
    public ModelView(String view) {
        this.view = view;
        this.model = new HashMap<>();
    }
    
    /**
     * Récupère l'URL de la vue
     * 
     * @return URL de la vue
     */
    public String getView() {
        return view;
    }
    
    /**
     * Récupère les données à passer à la vue
     * 
     * @return Map des données
     */
    public Map<String, Object> getModel() {
        return model;
    }
    
    /**
     * Ajoute une donnée à passer à la vue
     * 
     * @param key Nom de l'attribut dans la JSP
     * @param value Valeur de l'attribut
     */
    public void addItem(String key, Object value) {
        this.model.put(key, value);
    }
}
