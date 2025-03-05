<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Zone Admin - Zone Sécurisée</title>
    <style>
        .nav { margin: 20px 0; }
        .admin { color: red; }
    </style>
</head>
<body>
    <h1>Zone Admin - Zone Sécurisée</h1>
    
    <div class="nav">
        <a href="${pageContext.request.contextPath}/app/secured/home">Retour à l'accueil</a>
    </div>
    
    <div class="admin">
        <h2>Zone Administrative</h2>
        <p>Bienvenue administrateur ${username} !</p>
        <p>Cette page nécessite le rôle "admin" (surcharge l'authentification de la classe)</p>
    </div>
    
    <div>
        <a href="${pageContext.request.contextPath}/app/logout">Se déconnecter</a>
    </div>
</body>
</html>
