package com.framework.controller;

import com.framework.annotation.Controller;
import com.framework.annotation.GetMapping;
import com.framework.annotation.PostMapping;
import com.framework.annotation.UploadParam;
import com.framework.modelview.ModelView;
import com.framework.upload.WinterPart;

@Controller
public class FileUploadController {
    
    @GetMapping("/upload/form")
    public ModelView showUploadForm() {
        return new ModelView("file-upload.jsp");
    }
    
    @PostMapping("/upload/file")
    public ModelView handleFileUpload(@UploadParam("file") WinterPart file) {
        ModelView mv = new ModelView("file-upload.jsp");
        
        try {
            String fileName = file.save();
            mv.addItem("message", "Fichier uploadé avec succès : " + fileName);
            mv.addItem("fileUrl", "/sprint/static/" + fileName);
        } catch (Exception e) {
            mv.addItem("error", "Erreur lors de l'upload : " + e.getMessage());
        }
        
        return mv;
    }
}
