package com.framework.controller;

import com.framework.annotation.Controller;
import com.framework.annotation.GetMapping;
import com.framework.annotation.RequestParam;
import com.framework.model.User;

@Controller
public class Sprint7BisTestController {
    
    /**
     * Cette méthode devrait échouer car le paramètre name n'a pas d'annotation
     * Test URL: http://localhost:8080/sprint/app/sprint7bis/test1?name=John
     */
    @GetMapping("/sprint7bis/test1")
    public String test1(String name) {
        return "Hello " + name;
    }
    
    /**
     * Cette méthode devrait échouer car le paramètre age n'a pas d'annotation
     * Test URL: http://localhost:8080/sprint/app/sprint7bis/test2?age=25
     */
    @GetMapping("/sprint7bis/test2")
    public String test2(int age) {
        return "Age: " + age;
    }
    
    /**
     * Cette méthode devrait fonctionner car un des paramètres est un objet
     * Test URL: http://localhost:8080/sprint/app/sprint7bis/test3?firstName=John&lastName=Doe&role=admin
     */
    @GetMapping("/sprint7bis/test3")
    public String test3(User user, @RequestParam String role) {
        return "User: " + user + ", Role: " + role;
    }
    
    /**
     * Cette méthode devrait fonctionner car tous les paramètres primitifs ont @RequestParam
     * Test URL: http://localhost:8080/sprint/app/sprint7bis/test4?name=John&age=25
     */
    @GetMapping("/sprint7bis/test4")
    public String test4(@RequestParam String name, @RequestParam int age) {
        return "Name: " + name + ", Age: " + age;
    }
    
    /**
     * Cette méthode devrait échouer car isAdmin n'a pas d'annotation
     * Test URL: http://localhost:8080/sprint/app/sprint7bis/test5?name=John&isAdmin=true
     */
    @GetMapping("/sprint7bis/test5")
    public String test5(@RequestParam String name, boolean isAdmin) {
        return "Name: " + name + ", Is Admin: " + isAdmin;
    }
}
