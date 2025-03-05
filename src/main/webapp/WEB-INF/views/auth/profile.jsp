<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Profil</title>
    <style>
        .nav { margin: 20px 0; }
        .profile-info { margin: 20px 0; }
    </style>
</head>
<body>
    <h1>Profil de ${username}</h1>
    
    <div class="nav">
        <a href="${pageContext.request.contextPath}/app/dashboard">Tableau de bord</a>
        |
        <a href="${pageContext.request.contextPath}/app/logout">Déconnexion</a>
    </div>
    
    <div class="profile-info">
        <h2>Informations du profil</h2>
        <p>Cette page est également protégée par @Authenticated</p>
        <p>Elle utilise l'URL de redirection par défaut (/login)</p>
    </div>
</body>
</html>
