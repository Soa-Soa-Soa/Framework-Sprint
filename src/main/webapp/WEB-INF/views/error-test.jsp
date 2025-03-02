<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>Test des erreurs 404</title>
</head>
<body>
    <h1>Test des erreurs 404</h1>
    
    <h2>Test GET-only endpoint</h2>
    <a href="${pageContext.request.contextPath}/app/error-test/get-only">Test GET (devrait fonctionner)</a>
    <form action="${pageContext.request.contextPath}/app/error-test/get-only" method="post">
        <input type="submit" value="Test POST (devrait échouer avec 404)">
    </form>

    <h2>Test POST-only endpoint</h2>
    <a href="${pageContext.request.contextPath}/app/error-test/post-only">Test GET (devrait échouer avec 404)</a>
    <form action="${pageContext.request.contextPath}/app/error-test/post-only" method="post">
        <input type="submit" value="Test POST (devrait fonctionner)">
    </form>

    <h2>Test URL inexistante</h2>
    <a href="${pageContext.request.contextPath}/app/non-existent">Test URL inexistante (devrait échouer avec 404)</a>
</body>
</html>