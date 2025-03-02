package com.framework.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Servlet pour servir les fichiers statiques (images, documents, etc.)
 */
@WebServlet("/static/*")
public class StaticResourceServlet extends HttpServlet {
    private static final String UPLOAD_DIRECTORY = "uploads";
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        
        if (pathInfo == null || pathInfo.equals("/")) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        
        // Récupère le chemin du fichier demandé
        String uploadPath = getServletContext().getRealPath("") + File.separator + UPLOAD_DIRECTORY;
        Path filePath = Path.of(uploadPath, pathInfo.substring(1));
        
        // Vérifie si le fichier existe et est accessible
        if (!Files.exists(filePath) || !Files.isRegularFile(filePath)) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        
        // Vérifie que le fichier est bien dans le répertoire uploads
        String normalizedFilePath = filePath.normalize().toString();
        String normalizedUploadPath = Path.of(uploadPath).normalize().toString();
        if (!normalizedFilePath.startsWith(normalizedUploadPath)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        
        // Définit le type MIME
        String contentType = getServletContext().getMimeType(filePath.toString());
        if (contentType == null) {
            contentType = "application/octet-stream";
        }
        response.setContentType(contentType);
        
        // Envoie le fichier
        Files.copy(filePath, response.getOutputStream());
    }
}
