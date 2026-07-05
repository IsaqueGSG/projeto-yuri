<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>Recuperar Senha</title>
</head>
<body>
    <h2>Recuperar Senha</h2>
    
    <% if(request.getAttribute("mensagem") != null) { %>
        <p style="color: green;"><%= request.getAttribute("mensagem") %></p>
    <% } %>
    
    <% if(request.getParameter("erro") != null) { %>
        <p style="color: red;"><%= request.getParameter("erro") %></p>
    <% } %>

    <form action="solicitar-reset" method="post">
        <label>Digite seu e-mail cadastrado:</label><br>
        <input type="email" name="email" required><br><br>
        <button type="submit">Enviar Link de Recuperação</button>
    </form>
    <br>
    <a href="login.jsp">Voltar para o Login</a>
</body>
</html>