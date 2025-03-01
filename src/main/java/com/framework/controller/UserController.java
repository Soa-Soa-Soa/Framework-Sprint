package com.framework.controller;

import com.framework.annotation.Controller;
import com.framework.annotation.GetMapping;
import com.framework.model.User;
import com.framework.modelview.ModelView;
import java.util.ArrayList;
import java.util.List;

@Controller
public class UserController {
    
    /**
     * Exemple 1: Retourner une chaîne simple
     * Cette méthode affiche directement le texte dans le navigateur
     * URL: http://localhost:8080/sprint/app/users/count
     */
    @GetMapping("/users/count")
    public String getUserCount() {
        return "Nombre d'utilisateurs : 42";
    }

    /**
     * Test du binding d'objets
     * URL : http://localhost:8080/sprint/app/users/register?firstName=John&lastName=Doe&age=25&email=john@example.com
     */
    @GetMapping("/users/register")
    public String registerUser(User user) {
        return "Utilisateur enregistré : " + user.toString();
    }
    
    /**
     * Exemple 2: Retourner un ModelView avec des données
     * Cette méthode envoie les données à une page JSP pour affichage
     * URL: http://localhost:8080/sprint/app/users/list
     */
    @GetMapping("/users/list")
    public ModelView getUserList() {
        // 1. Créer l'objet ModelView en spécifiant la vue
        ModelView mv = new ModelView("users.jsp");
        
        // 2. Préparer les données
        List<String> users = new ArrayList<>();
        users.add("Alice");
        users.add("Bob");
        users.add("Charlie");
        
        // 3. Ajouter les données au ModelView
        mv.addItem("title", "Liste des utilisateurs");
        mv.addItem("users", users);
        mv.addItem("count", users.size());
        
        return mv;
    }
    
    private String[] users = {"Alice", "Bob", "Charlie"};
    
    public String getUsers() {
        return "Liste des utilisateurs : " + String.join(", ", users);
    }
    
    public String addUser(String name) {
        return "Ajout d'utilisateur : " + name;
    }
}
