package com.framework.controller;

import com.framework.annotation.Controller;
import com.framework.annotation.GetMapping;
import com.framework.annotation.RequestParam;
import com.framework.annotation.SessionInject;
import com.framework.modelview.ModelView;
import com.framework.session.Session;

@Controller
public class SessionTestController {
    
    @SessionInject
    private Session session;
    
    /**
     * Test de stockage dans la session
     * URL: http://localhost:8080/sprint/app/session/set?name=John&value=Doe
     */
    @GetMapping("/session/set")
    public String setSessionAttribute(
        @RequestParam(name = "name") String name,
        @RequestParam(name = "value") String value
    ) {
        session.setAttribute(name, value);
        return "Attribut '" + name + "' défini avec la valeur '" + value + "'";
    }
    
    /**
     * Test de lecture depuis la session
     * URL: http://localhost:8080/sprint/app/session/get?name=John
     */
    @GetMapping("/session/get")
    public String getSessionAttribute(@RequestParam(name = "name") String name) {
        Object value = session.getAttribute(name);
        if (value == null) {
            return "Aucun attribut trouvé pour '" + name + "'";
        }
        return "Valeur de '" + name + "' : " + value.toString();
    }
    
    /**
     * Test d'affichage dans une JSP
     * URL: http://localhost:8080/sprint/app/session/view
     */
    @GetMapping("/session/view")
    public ModelView viewSession() {
        ModelView mv = new ModelView("session.jsp");
        mv.addItem("sessionId", session.getId());
        mv.addItem("lastAccess", new java.util.Date(session.getLastAccessTime()));
        return mv;
    }
    
    /**
     * Test de suppression d'un attribut
     * URL: http://localhost:8080/sprint/app/session/remove?name=John
     */
    @GetMapping("/session/remove")
    public String removeSessionAttribute(@RequestParam(name = "name") String name) {
        session.removeAttribute(name);
        return "Attribut '" + name + "' supprimé";
    }
    
    /**
     * Test d'invalidation de la session
     * URL: http://localhost:8080/sprint/app/session/invalidate
     */
    @GetMapping("/session/invalidate")
    public String invalidateSession() {
        session.invalidate();
        return "Session invalidée";
    }
}
