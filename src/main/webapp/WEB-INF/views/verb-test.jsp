<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Test des verbes HTTP</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            max-width: 800px;
            margin: 20px auto;
            padding: 20px;
        }
        .form-section {
            margin-bottom: 30px;
            padding: 20px;
            border: 1px solid #ddd;
            border-radius: 5px;
        }
        .response {
            margin-top: 20px;
            padding: 10px;
            background-color: #f0f0f0;
            border-radius: 5px;
        }
        input[type="text"] {
            padding: 5px;
            width: 200px;
            margin-right: 10px;
        }
        input[type="submit"] {
            padding: 5px 15px;
            background-color: #4CAF50;
            color: white;
            border: none;
            border-radius: 3px;
            cursor: pointer;
        }
        input[type="submit"]:hover {
            background-color: #45a049;
        }
    </style>
</head>
<body>
    <h1>Test des verbes HTTP</h1>
    
    <div class="form-section">
        <h2>Test GET</h2>
        <form action="${pageContext.request.contextPath}/app/verb/get-test" method="get">
            <input type="text" name="message" placeholder="Entrez un message">
            <input type="submit" value="Envoyer GET">
        </form>
    </div>

    <div class="form-section">
        <h2>Test POST</h2>
        <form action="${pageContext.request.contextPath}/app/verb/post-test" method="post">
            <input type="text" name="message" placeholder="Entrez un message">
            <input type="submit" value="Envoyer POST">
        </form>
    </div>

    <% if (request.getAttribute("responseMessage") != null) { %>
        <div class="response">
            <h3>Réponse du serveur :</h3>
            <p>${responseMessage}</p>
        </div>
    <% } %>
</body>
</html>
