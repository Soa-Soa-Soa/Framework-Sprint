<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Inscription</title>
    <style>
        .form-group { margin: 15px 0; }
        .error { color: red; font-size: 0.9em; }
        label { display: block; margin-bottom: 5px; }
        input { padding: 5px; width: 200px; }
        button { margin-top: 10px; padding: 8px 15px; }
    </style>
</head>
<body>
    <h1>Inscription</h1>
    
    <form action="${pageContext.request.contextPath}/app/register" method="post">
        <div class="form-group">
            <label>Nom :</label>
            <input type="text" name="name" value="${values.name}">
            <span class="error">${errors.name}</span>
        </div>
        
        <div class="form-group">
            <label>Age :</label>
            <input type="number" name="age" value="${values.age}">
            <span class="error">${errors.age}</span>
        </div>
        
        <div class="form-group">
            <label>Email :</label>
            <input type="email" name="email" value="${values.email}">
            <span class="error">${errors.email}</span>
        </div>
        
        <div class="form-group">
            <label>Note (0-5) :</label>
            <input type="number" step="0.1" name="rating" value="${values.rating}">
            <span class="error">${errors.rating}</span>
        </div>
        
        <button type="submit">S'inscrire</button>
    </form>
</body>
</html>
