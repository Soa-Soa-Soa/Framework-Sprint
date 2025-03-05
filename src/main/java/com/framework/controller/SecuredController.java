package com.framework.controller;

import com.framework.annotation.*;
import com.framework.modelview.*;
import com.framework.session.Session;
import com.framework.auth.AuthenticationManager;

/**
 * Contrôleur de test pour démontrer l'authentification basée sur le contrôleur.
 * Toutes les méthodes nécessitent une authentification sauf celles marquées @Public
 */
@Controller
@Authenticated("user")  // Toutes les méthodes nécessitent le rôle "user" par défaut
public class SecuredController {
    
    @SessionInject
    private Session session;
    
    @GetMapping("/secured/home")
    @Public  // Cette méthode est accessible sans authentification
    public ModelView home() {
        ModelView mv = new ModelView("secured/home.jsp");
        String username = (String) session.getAttribute(AuthenticationManager.USER_KEY);
        mv.addItem("username", username != null ? username : "Invité");
        return mv;
    }
    
    @GetMapping("/secured/profile")
    // Hérite de @Authenticated("user") de la classe
    public ModelView profile() {
        ModelView mv = new ModelView("secured/profile.jsp");
        mv.addItem("username", session.getAttribute(AuthenticationManager.USER_KEY));
        mv.addItem("role", session.getAttribute(AuthenticationManager.ROLE_KEY));
        return mv;
    }
    
    @GetMapping("/secured/admin")
    @Authenticated("admin")  // Surcharge le rôle requis pour cette méthode
    public ModelView adminArea() {
        ModelView mv = new ModelView("secured/admin.jsp");
        mv.addItem("username", session.getAttribute(AuthenticationManager.USER_KEY));
        return mv;
    }
    
    @GetMapping("/secured/about")
    @Public  // Cette méthode est accessible sans authentification
    public ModelView about() {
        return new ModelView("secured/about.jsp");
    }
}
