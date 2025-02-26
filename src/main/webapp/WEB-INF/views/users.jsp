<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>${title}</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 40px;
        }
        .user-list {
            list-style-type: none;
            padding: 0;
        }
        .user-item {
            padding: 10px;
            margin: 5px 0;
            background-color: #f5f5f5;
            border-radius: 5px;
        }
        .count {
            color: #666;
            font-style: italic;
        }
    </style>
</head>
<body>
    <h1>${title}</h1>
    
    <ul class="user-list">
        <% 
        List<String> users = (List<String>) request.getAttribute("users");
        for(String user : users) {
        %>
            <li class="user-item"><%= user %></li>
        <% } %>
    </ul>
    
    <p class="count">Nombre total d'utilisateurs : ${count}</p>
</body>
</html>
