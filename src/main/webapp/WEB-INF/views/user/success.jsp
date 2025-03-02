<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Inscription Réussie</title>
    <style>
        .success { color: green; }
        .user-info { margin: 20px 0; }
        .back-link { margin-top: 20px; }
    </style>
</head>
<body>
    <h1>Inscription Réussie !</h1>
    
    <div class="user-info">
        <h2>Informations enregistrées :</h2>
        <p><strong>Nom :</strong> ${name}</p>
        <p><strong>Age :</strong> ${age} ans</p>
        <p><strong>Email :</strong> ${email}</p>
        <p><strong>Note :</strong> ${rating}/5</p>
    </div>
    
    <div class="back-link">
        <form action="${pageContext.request.contextPath}/app/register" method="get">
            <input type="hidden" name="name" value="${values.name}">
            <input type="hidden" name="age" value="${values.age}">
            <input type="hidden" name="email" value="${values.email}">
            <input type="hidden" name="rating" value="${values.rating}">
            <button type="submit">Retour au formulaire</button>
        </form>
    </div>
</body>
</html>
