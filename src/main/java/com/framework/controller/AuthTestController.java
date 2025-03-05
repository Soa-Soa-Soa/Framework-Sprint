package com.framework.controller;

import com.framework.annotation.*;
import com.framework.modelview.*;
import com.framework.session.Session;
import com.framework.validation.NotBlank;
import com.framework.auth.AuthenticationManager;

@Controller
public class AuthTestController {
    @SessionInject
    private Session session;

    @GetMapping("/dashboard")
    @Authenticated("admin")
    public ModelView adminDashboard() {
        return new ModelView("auth/dashboard.jsp");
    }

    @GetMapping("/profile")
    @Authenticated("user")
    public ModelView userProfile() {
        return new ModelView("auth/profile.jsp");
    }

    @GetMapping("/login/form")
    public ModelView form(){
        ModelView mv = new ModelView("auth/login.jsp");
        return mv;
    }

    @PostMapping("/login")
    public ModelView login(
        @RequestParam(name = "username") @NotBlank(message="Nom d'utilisateur requis") String username,
        @RequestParam(name = "password") @NotBlank(message="Mot de passe requis") String password
    ) {
        if (username.equals("admin") && password.equals("admin123")) {
            AuthenticationManager.authenticate(session, username, "admin");
            return new ModelView("secured/admin.jsp");
        } else if (username.equals("user") && password.equals("user123")) {
            AuthenticationManager.authenticate(session, username, "user");
            return new ModelView("secured/profile.jsp");
        }
        
        ModelView mv = new ModelView("auth/login.jsp");
        mv.addItem("error", "Identifiants invalides");
        return mv;
    }

    @GetMapping("/logout")
    public ModelView logout() {
        AuthenticationManager.logout(session);
        return new ModelView("auth/login.jsp");
    }
}

