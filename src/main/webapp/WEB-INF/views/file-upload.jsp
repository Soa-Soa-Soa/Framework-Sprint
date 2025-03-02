<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Upload de fichiers</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            max-width: 800px;
            margin: 20px auto;
            padding: 20px;
        }
        .upload-form {
            border: 2px dashed #ccc;
            padding: 20px;
            text-align: center;
            margin: 20px 0;
        }
        .message {
            padding: 10px;
            margin: 10px 0;
            border-radius: 4px;
        }
        .success {
            background-color: #d4edda;
            color: #155724;
            border: 1px solid #c3e6cb;
        }
        .error {
            background-color: #f8d7da;
            color: #721c24;
            border: 1px solid #f5c6cb;
        }
        .preview {
            margin-top: 20px;
            padding: 10px;
            background-color: #f8f9fa;
            border-radius: 4px;
        }
        img.preview-image {
            max-width: 100%;
            max-height: 300px;
            margin-top: 10px;
        }
    </style>
</head>
<body>
    <h1>Upload de fichiers</h1>
    
    <div class="upload-form">
        <form action="${pageContext.request.contextPath}/app/upload/file" method="post" enctype="multipart/form-data">
            <input type="file" name="file" required>
            <br><br>
            <input type="submit" value="Uploader">
        </form>
    </div>

    <% if (request.getAttribute("message") != null) { %>
        <div class="message success">
            ${message}
            <% if (request.getAttribute("fileUrl") != null) { %>
                <div class="preview">
                    <p>URL du fichier : <a href="${fileUrl}" target="_blank">${fileUrl}</a></p>
                    <% 
                        String fileUrl = (String) request.getAttribute("fileUrl");
                        String extension = fileUrl.substring(fileUrl.lastIndexOf('.') + 1).toLowerCase();
                        if (extension.matches("jpg|jpeg|png|gif")) {
                    %>
                        <img src="${fileUrl}" alt="Preview" class="preview-image">
                    <% } %>
                </div>
            <% } %>
        </div>
    <% } %>

    <% if (request.getAttribute("error") != null) { %>
        <div class="message error">
            ${error}
        </div>
    <% } %>
</body>
</html>
