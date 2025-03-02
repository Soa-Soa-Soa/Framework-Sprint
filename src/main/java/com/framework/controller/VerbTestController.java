package com.framework.controller;

import com.framework.annotation.Controller;
import com.framework.annotation.GetMapping;
import com.framework.annotation.PostMapping;
import com.framework.annotation.RequestParam;
import com.framework.modelview.ModelView;

@Controller
public class VerbTestController {
    
    @GetMapping("/verb/form")
    public ModelView showForm() {
        return new ModelView("verb-test.jsp");
    }
    
    @GetMapping("/verb/get-test")
    public ModelView testGet(@RequestParam(name = "message") String message) {
        ModelView mv = new ModelView("verb-test.jsp");
        mv.addItem("responseMessage", "GET reçu avec message: " + message);
        return mv;
    }
    
    @PostMapping("/verb/post-test")
    public ModelView testPost(@RequestParam(name = "message") String message) {
        ModelView mv = new ModelView("verb-test.jsp");
        mv.addItem("responseMessage", "POST reçu avec message: " + message);
        return mv;
    }
}
