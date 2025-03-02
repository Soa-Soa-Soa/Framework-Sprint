package com.framework.session;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Gestionnaire de sessions
 */
public class SessionManager {
    private static final String SESSION_COOKIE_NAME = "SESSIONID";
    private static final Map<String, Session> sessions = new ConcurrentHashMap<>();
    
    /**
     * Récupère ou crée une session pour une requête
     */
    public static Session getSession(HttpServletRequest request, HttpServletResponse response, boolean create) {
        String sessionId = null;
        Cookie[] cookies = request.getCookies();
        
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (SESSION_COOKIE_NAME.equals(cookie.getName())) {
                    sessionId = cookie.getValue();
                    break;
                }
            }
        }
        
        Session session = null;
        if (sessionId != null) {
            session = sessions.get(sessionId);
            if (session != null && session.isExpired()) {
                sessions.remove(sessionId);
                session = null;
            }
        }
        
        if (session == null && create) {
            session = new Session();
            sessions.put(session.getId(), session);
            Cookie sessionCookie = new Cookie(SESSION_COOKIE_NAME, session.getId());
            sessionCookie.setPath("/");
            response.addCookie(sessionCookie);
        }
        
        return session;
    }
    
    /**
     * Invalide une session
     */
    public static void invalidateSession(String sessionId) {
        Session session = sessions.get(sessionId);
        if (session != null) {
            session.invalidate();
            sessions.remove(sessionId);
        }
    }
    
    /**
     * Nettoie les sessions expirées
     */
    public static void cleanExpiredSessions() {
        sessions.entrySet().removeIf(entry -> entry.getValue().isExpired());
    }
}
