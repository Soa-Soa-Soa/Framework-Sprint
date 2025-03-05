<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Profil - Zone Sécurisée</title>
    <style>
        .nav { margin: 20px 0; }
        .info { margin: 20px 0; }
    </style>
</head>
<body>
    <h1>Profil - Zone Sécurisée</h1>
    
    <div class="nav">
        <a href="${pageContext.request.contextPath}/app/secured/home">Retour à l'accueil</a>
    </div>
    
    <div class="info">
        <h2>Informations du profil</h2>
        <p><strong>Nom d'utilisateur :</strong> ${username}</p>
        <p><strong>Rôle :</strong> ${role}</p>
    </div>
    
    <div>
        <p>Cette page nécessite le rôle "user" (hérité de la classe)</p>
        <a href="${pageContext.request.contextPath}/app/logout">Se déconnecter</a>
    </div>
</body>
</html>
