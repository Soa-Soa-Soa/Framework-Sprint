package com.framework.controller;

import com.framework.annotation.*;
import com.framework.validation.*;
import com.framework.modelview.ModelView;

@Controller
public class UserRegistrationController {
    
    @GetMapping("/register")
    public ModelView showForm(
        @RequestParam(name = "name", required = false) String name,
        @RequestParam(name = "age", required = false) Integer age,
        @RequestParam(name = "email", required = false) String email,
        @RequestParam(name = "rating", required = false) Double rating
    ) {
        ModelView mv = new ModelView("user/register.jsp");
        
        // Si des valeurs sont passées en paramètre, on les stocke
        if (name != null || age != null || email != null || rating != null) {
            ValidationErrors values = new ValidationErrors();
            if (name != null) values.addValue("name", name);
            if (age != null) values.addValue("age", age.toString());
            if (email != null) values.addValue("email", email);
            if (rating != null) values.addValue("rating", rating.toString());
            mv.addItem("values", values.getValues());
        }
        
        return mv;
    }
    
    @PostMapping("/register")
    @ErrorURL("/user/register")
    public ModelView register(
        @RequestParam(name = "name") @NotBlank(message="Le nom est requis") String name,
        @RequestParam(name = "age") @Required @Range(min=13, max=120, message="L'âge doit être entre {min} et {max} ans") Integer age,
        @RequestParam(name = "email") @Required @NotBlank(message="Email invalide") String email,
        @RequestParam(name = "rating") @Range(min=0, max=5, message="La note doit être entre {min} et {max}") Double rating
    ) {
        // Si on arrive ici, c'est que la validation a réussi
        ModelView mv = new ModelView("user/success.jsp");
        
        // Ajoute les valeurs pour pouvoir les réafficher
        mv.addItem("name", name);
        mv.addItem("age", age);
        mv.addItem("email", email);
        mv.addItem("rating", rating);
        
        // Stocke aussi les valeurs dans values pour le formulaire
        mv.addItem("values", new ValidationErrors() {{
            addValue("name", name);
            addValue("age", age.toString());
            addValue("email", email);
            addValue("rating", rating.toString());
        }}.getValues());
        
        return mv;
    }
}
