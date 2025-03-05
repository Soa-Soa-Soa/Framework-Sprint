<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Tableau de bord</title>
    <style>
        .nav { margin: 20px 0; }
        .welcome { color: green; }
    </style>
</head>
<body>
    <h1>Tableau de bord</h1>
    
    <div class="welcome">
        Bienvenue ${username} !
    </div>
    
    <div class="nav">
        <a href="${pageContext.request.contextPath}/app/profile">Mon profil</a>
        |
        <a href="${pageContext.request.contextPath}/app/logout">Déconnexion</a>
    </div>
    
    <div class="content">
        <h2>Zone protégée</h2>
        <p>Cette page n'est accessible qu'aux utilisateurs authentifiés.</p>
    </div>
</body>
</html>
