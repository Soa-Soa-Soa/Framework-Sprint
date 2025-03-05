<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>À propos - Zone Sécurisée</title>
    <style>
        .nav { margin: 20px 0; }
        .public { color: blue; }
    </style>
</head>
<body>
    <h1>À propos - Zone Sécurisée</h1>
    
    <div class="nav">
        <a href="${pageContext.request.contextPath}/app/secured/home">Retour à l'accueil</a>
    </div>
    
    <div>
        <h2>À propos de cette démonstration</h2>
        <p class="public">Cette page est publique (marquée avec @Public)</p>
        <p>Cette démonstration montre comment :</p>
        <ul>
            <li>Utiliser @Authenticated au niveau de la classe pour sécuriser toutes les méthodes</li>
            <li>Utiliser @Public pour rendre certaines méthodes accessibles sans authentification</li>
            <li>Surcharger le rôle requis pour certaines méthodes avec @Authenticated</li>
        </ul>
    </div>
</body>
</html>
