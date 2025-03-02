package com.framework.controller;

import com.framework.annotation.*;
import com.framework.validation.*;
import com.framework.modelview.ModelView;

@RestController
public class ValidationTestController {

    // Test de @Required
    @GetMapping("/validation/required")
    public String testRequired(
        @RequestParam(name = "id") @Required Integer id
    ) {
        return "ID valide : " + id;
    }
    
    // Test de @NotBlank
    @GetMapping("/validation/notblank")
    public String testNotBlank(
        @RequestParam(name = "name") @NotBlank String name
    ) {
        return "Nom valide : " + name;
    }
    
    // Test de @Range
    @GetMapping("/validation/range")
    public String testRange(
        @RequestParam(name = "score") @Range(min = 0, max = 100) Integer score
    ) {
        return "Score valide : " + score;
    }
    
    // Test combiné de plusieurs validations
    @PostMapping("/validation/user")
    public ModelView testComplexValidation(
        @RequestParam(name = "username") @NotBlank String username,
        @RequestParam(name = "age") @Required @Range(min = 18, max = 100) Integer age,
        @RequestParam(name = "rating") @Range(min = 0, max = 5) Double rating
    ) {
        ModelView mv = new ModelView("validation/success");
        mv.addItem("username", username);
        mv.addItem("age", age);
        mv.addItem("rating", rating);
        return mv;
    }
}