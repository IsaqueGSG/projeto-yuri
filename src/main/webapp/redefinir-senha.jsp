<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>Definir Nova Senha</title>
</head>
<body>
    <h2>Cadastrar Nova Senha</h2>
    
    <form action="redefinir-senha" method="post">
        <input type="hidden" name="token" value="<%= request.getAttribute("token") != null ? request.getAttribute("token") : request.getParameter("token") %>">
        
        <label>Digite sua nova senha:</label><br>
        <input type="password" name="senha" required minlength="6"><br><br>
        
        <button type="submit">Atualizar Senha</button>
    </form>
</body>
</html>