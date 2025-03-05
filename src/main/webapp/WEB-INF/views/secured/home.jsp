<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Accueil - Zone Sécurisée</title>
    <style>
        .nav { margin: 20px 0; }
        .welcome { color: green; }
        .public { color: blue; }
    </style>
</head>
<body>
    <h1>Accueil - Zone Sécurisée</h1>
    
    <div class="welcome">
        Bienvenue ${username} !
    </div>
    
    <div class="nav">
        <h3>Navigation :</h3>
        <ul>
            <li class="public"><a href="${pageContext.request.contextPath}/app/secured/about">À propos (Public)</a></li>
            <li><a href="${pageContext.request.contextPath}/app/secured/profile">Mon Profil (Utilisateur)</a></li>
            <li><a href="${pageContext.request.contextPath}/app/secured/admin">Zone Admin (Admin uniquement)</a></li>
        </ul>
    </div>
    
    <div>
        <p class="public">Cette page est publique (marquée avec @Public)</p>
        <p>Les autres pages nécessitent une authentification avec des rôles spécifiques.</p>
    </div>
    
    <% if (request.getSession().getAttribute("user") == null) { %>
        <div>
            <a href="${pageContext.request.contextPath}/app/login">Se connecter</a>
        </div>
    <% } else { %>
        <div>
            <a href="${pageContext.request.contextPath}/app/logout">Se déconnecter</a>
        </div>
    <% } %>
</body>
</html>
