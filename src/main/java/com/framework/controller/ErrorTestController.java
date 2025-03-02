package com.framework.controller;

import com.framework.annotation.Controller;
import com.framework.annotation.GetMapping;
import com.framework.annotation.PostMapping;
import com.framework.modelview.ModelView;

@Controller
public class ErrorTestController {

    @GetMapping("/error-test/form")
    public ModelView showForm() {
        return new ModelView("error-test.jsp");
    }
    
    // Cette méthode ne supporte que GET
    @GetMapping("/error-test/get-only")
    public ModelView getOnlyEndpoint() {
        ModelView mv = new ModelView("verb-test.jsp");
        mv.addItem("responseMessage", "GET request successful");
        return mv;
    }

    // Cette méthode ne supporte que POST
    @PostMapping("/error-test/post-only")
    public ModelView postOnlyEndpoint() {
        ModelView mv = new ModelView("verb-test.jsp");
        mv.addItem("responseMessage", "POST request successful");
        return mv;
    }
}
