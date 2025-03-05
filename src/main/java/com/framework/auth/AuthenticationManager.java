package com.framework.auth;

import com.framework.session.Session;

/**
 * Gestionnaire d'authentification qui vérifie si l'utilisateur est authentifié
 */
public class AuthenticationManager {
    public static final String USER_KEY = "user";
    public static final String ROLE_KEY = "role";

    /**
     * Authentifie un utilisateur en stockant ses informations dans la session
     */
    public static void authenticate(Session session, String username, String role) {
        session.setAttribute(USER_KEY, username);
        session.setAttribute(ROLE_KEY, role);
    }

    public static boolean hasRole(Session session, String requiredRole) {
        if (session == null) return false;
        String userRole = (String) session.getAttribute(ROLE_KEY);
        return requiredRole.isEmpty() || (userRole != null && userRole.equals(requiredRole));
    }
    
    /**
     * Déconnecte l'utilisateur en supprimant ses informations de la session
     */
    public static void logout(Session session) {
        session.invalidate();
    }
}
