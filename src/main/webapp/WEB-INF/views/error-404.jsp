<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>404 - Non trouvé</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            max-width: 800px;
            margin: 50px auto;
            padding: 20px;
            text-align: center;
        }
        .error-code {
            font-size: 72px;
            color: #e74c3c;
            margin: 0;
        }
        .error-message {
            font-size: 24px;
            color: #333;
            margin: 20px 0;
        }
        .error-details {
            color: #666;
            margin: 20px 0;
            padding: 15px;
            background-color: #f8f9fa;
            border-radius: 5px;
        }
        .home-link {
            display: inline-block;
            margin-top: 20px;
            padding: 10px 20px;
            background-color: #3498db;
            color: white;
            text-decoration: none;
            border-radius: 5px;
        }
        .home-link:hover {
            background-color: #2980b9;
        }
    </style>
</head>
<body>
    <h1 class="error-code">404</h1>
    <h2 class="error-message">Page non trouvée</h2>
    <div class="error-details">
        ${errorMessage}
    </div>
    <a href="${pageContext.request.contextPath}/app/" class="home-link">Retour à l'accueil</a>
</body>
</html>
