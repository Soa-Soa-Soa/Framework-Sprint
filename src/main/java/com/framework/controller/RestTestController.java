package com.framework.controller;

import com.framework.annotation.RestController;
import com.framework.annotation.RestEndPoint;
import com.framework.modelview.ModelView;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

@RestController
public class RestTestController {
    
    /**
     * http://localhost:8080/sprint/app/api/string
     * @return
     */
    @RestEndPoint("/api/string")
    public String testString() {
        return "Hello from REST API!";
    }
    
    /**
     * http://localhost:8080/sprint/app/api/object
     * @return
     */
    @RestEndPoint("/api/object")
    public Person testObject() {
        return new Person("John Doe", 25);
    }
    
    /**
     * http://localhost:8080/sprint/app/api/list
     * @return
     */
    @RestEndPoint("/api/list")
    public List<Person> testList() {
        List<Person> people = new ArrayList<>();
        people.add(new Person("John Doe", 25));
        people.add(new Person("Jane Doe", 23));
        return people;
    }
    
    /**
     * http://localhost:8080/sprint/app/api/modelview
     */
    @RestEndPoint("/api/modelview")
    public ModelView testModelView() {
        ModelView mv = new ModelView("rest");
        Map<String, Object> data = new HashMap<>();
        data.put("message", "Hello from ModelView!");
        data.put("timestamp", System.currentTimeMillis());
        mv.addItem("data", data);
        return mv;
    }
    
    // Classe interne pour les tests
    public static class Person {
        private String name;
        private int age;
        
        public Person(String name, int age) {
            this.name = name;
            this.age = age;
        }
        
        // Getters nécessaires pour la sérialisation JSON
        public String getName() { return name; }
        public int getAge() { return age; }
    }
}