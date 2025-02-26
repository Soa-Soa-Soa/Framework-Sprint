package com.framework.controller;

import com.framework.annotation.Controller;
import com.framework.annotation.GetMapping;

@Controller
public class TestController {
    
    @GetMapping("/")
    public String index() {
        return "Page d'accueil";
    }
    
    @GetMapping("/test")
    public String test() {
        return "Page de test";
    }
    
    @GetMapping("/hello")
    public String hello() {
        return "Hello World!";
    }
}
