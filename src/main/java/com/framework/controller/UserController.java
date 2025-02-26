package com.framework.controller;

import com.framework.annotation.Controller;

@Controller
public class UserController {
    private String[] users = {"Alice", "Bob", "Charlie"};
    
    public String getUsers() {
        return "Liste des utilisateurs : " + String.join(", ", users);
    }
    
    public String addUser(String name) {
        return "Ajout d'utilisateur : " + name;
    }
}
