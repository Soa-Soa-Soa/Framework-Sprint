<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Connexion</title>
    <style>
        .error { color: red; }
        .form-group { margin: 10px 0; }
    </style>
</head>
<body>
    <h1>Connexion</h1>
    
    <% if (request.getAttribute("error") != null) { %>
        <div class="error">${error}</div>
    <% } %>
    
    <form action="${pageContext.request.contextPath}/app/login" method="post">
        <div class="form-group">
            <label for="username">Nom d'utilisateur:</label>
            <input type="text" id="username" name="username" value="${values.username}">
            <span class="error">${errors.username}</span>
        </div>
        
        <div class="form-group">
            <label for="password">Mot de passe:</label>
            <input type="password" id="password" name="password">
            <span class="error">${errors.password}</span>
        </div>
        
        <button type="submit">Se connecter</button>
    </form>
</body>
</html>
