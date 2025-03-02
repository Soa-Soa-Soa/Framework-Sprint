package com.framework.upload;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import jakarta.servlet.http.Part;

/**
 * Représente un fichier uploadé dans le framework
 */
public class WinterPart {
    private Part part;
        private String uploadDirectory;
            private String savedFileName;
        
            public WinterPart() {
                this.part = null;
                this.uploadDirectory = null;
                this.savedFileName = null;
            }
            
            public WinterPart(Part part, String uploadDirectory) {
                this.part = part;
                this.uploadDirectory = uploadDirectory;
                this.savedFileName = extractFileName(part);
            }
        
            /**
             * Extrait le nom du fichier à partir du Part
             */
            private String extractFileName(Part part) {
                if (part == null) return null;
                
                String contentDisp = part.getHeader("content-disposition");
                String[] items = contentDisp.split(";");
                
                for (String item : items) {
                    if (item.trim().startsWith("filename")) {
                        return item.substring(item.indexOf("=") + 2, item.length() - 1);
                    }
                }
                return null;
            }
            
            /**
             * Obtient le nom original du fichier
             */
            public String getFileName() {
                String contentDisposition = part.getHeader("content-disposition");
                String[] items = contentDisposition.split(";");
                
                for (String item : items) {
                    if (item.trim().startsWith("filename")) {
                        return item.substring(item.indexOf('=') + 2, item.length() - 1);
                    }
                }
                return null;
            }
            
            /**
             * Obtient le type MIME du fichier
             */
            public String getContentType() {
                return part.getContentType();
            }
            
            /**
             * Obtient la taille du fichier
             */
            public long getSize() {
                return part.getSize();
            }
            
            /**
             * Obtient le flux d'entrée pour lire le contenu du fichier
             */
            public InputStream getInputStream() throws IOException {
                return part.getInputStream();
            }
            
            /**
             * Sauvegarde le fichier avec son nom original
             */
            public String save() throws IOException {
                return save(getFileName());
            }
            
            /**
             * Sauvegarde le fichier avec un nom spécifique
             */
            public String save(String fileName) throws IOException {
                // Crée le répertoire s'il n'existe pas
                File uploadDir = new File(uploadDirectory);
                if (!uploadDir.exists()) {
                    uploadDir.mkdirs();
                }
                
                // Génère un nom de fichier unique si nécessaire
                String uniqueFileName = getUniqueFileName(fileName);
                Path targetPath = Path.of(uploadDirectory, uniqueFileName);
                
                // Copie le fichier
                try (InputStream input = part.getInputStream()) {
                    Files.copy(input, targetPath, StandardCopyOption.REPLACE_EXISTING);
                }
                
                this.savedFileName = uniqueFileName;
                return uniqueFileName;
            }
            
            /**
             * Obtient le chemin complet du fichier sauvegardé
             */
            public String getSavedPath() {
                if (savedFileName == null) {
                    return null;
                }
                return Path.of(uploadDirectory, savedFileName).toString();
            }
            
            /**
             * Supprime le fichier s'il a été sauvegardé
             */
            public boolean delete() {
                if (savedFileName == null) {
                    return false;
                }
                
                Path filePath = Path.of(uploadDirectory, savedFileName);
                try {
                    return Files.deleteIfExists(filePath);
                } catch (IOException e) {
                    return false;
                }
            }
            
            private String getUniqueFileName(String originalFileName) {
                String baseName = originalFileName;
                String extension = "";
                
                int dotIndex = originalFileName.lastIndexOf('.');
                if (dotIndex > 0) {
                    baseName = originalFileName.substring(0, dotIndex);
                    extension = originalFileName.substring(dotIndex);
                }
                
                String fileName = originalFileName;
                int counter = 1;
                
                while (Files.exists(Path.of(uploadDirectory, fileName))) {
                    fileName = baseName + "_" + counter + extension;
                    counter++;
                }
                
                return fileName;
            }
        
            public String getSavedFileName() {
                return savedFileName;
            }
            
            public void setSavedFileName(String savedFileName) {
                this.savedFileName = savedFileName;
            }
            
            public Part getPart() {
                return part;
            }
            
            public void setPart(Part part) {
                this.part = part;
            if (part != null) {
                this.savedFileName = extractFileName(part);
            }
        }
        
        public String getUploadDirectory() {
            return uploadDirectory;
        }
        
        public void setUploadDirectory(String uploadDirectory) {
            this.uploadDirectory = uploadDirectory;
    }
}
