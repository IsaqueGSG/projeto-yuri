<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="pt-BR">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Definir Nova Senha - Sistema Imobiliária</title>
    <style>
        * {
            box-sizing: border-box;
            margin: 0;
            padding: 0;
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
        }

        body {
            background-color: #f1f5f9;
            display: flex;
            justify-content: center;
            align-items: center;
            min-height: 100vh;
            color: #334155;
        }

        .card {
            background-color: #ffffff;
            padding: 40px 30px;
            border-radius: 12px;
            box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.05), 0 4px 6px -4px rgba(0, 0, 0, 0.05);
            width: 100%;
            max-width: 400px;
            border: 1px solid #e2e8f0;
        }

        .card-header {
            text-align: center;
            margin-bottom: 24px;
        }

        .card-header h2 {
            color: #0f172a;
            font-size: 24px;
            font-weight: 600;
            margin-bottom: 8px;
        }

        .card-header p {
            color: #64748b;
            font-size: 14px;
        }

        /* Suporte para mensagens de alerta se precisar exibir erros nesta tela futuramente */
        .alert {
            padding: 12px 16px;
            border-radius: 8px;
            font-size: 14px;
            margin-bottom: 20px;
            line-height: 1.5;
        }

        .alert-error {
            background-color: #fef2f2;
            color: #991b1b;
            border: 1px solid #fecaca;
        }

        .form-group {
            margin-bottom: 20px;
        }

        .form-group label {
            display: block;
            font-size: 14px;
            font-weight: 500;
            color: #475569;
            margin-bottom: 8px;
        }

        .form-group input {
            width: 100%;
            padding: 12px 14px;
            border: 1px solid #cbd5e1;
            border-radius: 8px;
            font-size: 15px;
            transition: border-color 0.2s, box-shadow 0.2s;
            outline: none;
            color: #1e293b;
        }

        .form-group input:focus {
            border-color: #2563eb;
            box-shadow: 0 0 0 3px rgba(37, 99, 235, 0.15);
        }

        .requirements {
            font-size: 12px;
            color: #64748b;
            margin-top: 6px;
        }

        .btn-submit {
            width: 100%;
            background-color: #2563eb;
            color: #ffffff;
            border: none;
            padding: 12px;
            font-size: 15px;
            font-weight: 600;
            border-radius: 8px;
            cursor: pointer;
            transition: background-color 0.2s;
        }

        .btn-submit:hover {
            background-color: #1d4ed8;
        }

        .card-footer {
            text-align: center;
            margin-top: 24px;
            border-top: 1px solid #f1f5f9;
            padding-top: 16px;
        }

        .card-footer a {
            color: #2563eb;
            text-decoration: none;
            font-size: 14px;
            font-weight: 500;
            transition: color 0.2s;
        }

        .card-footer a:hover {
            color: #1d4ed8;
            text-decoration: underline;
        }
    </style>
</head>
<body>

    <div class="card">
        <div class="card-header">
            <h2>Cadastrar Nova Senha</h2>
            <p>Escolha uma combinação segura para proteger sua conta.</p>
        </div>
        
        <% if(request.getParameter("erro") != null) { %>
            <div class="alert alert-error">
                <%= request.getParameter("erro") %>
            </div>
        <% } %>

        <form action="redefinir-senha" method="post">
            <input type="hidden" name="token" value="<%= request.getAttribute("token") != null ? request.getAttribute("token") : request.getParameter("token") %>">
            
            <div class="form-group">
                <label for="senha">Nova Senha de Acesso</label>
                <input type="password" id="senha" name="senha" placeholder="••••••••" required minlength="6">
                <p class="requirements">A senha deve conter no mínimo 6 caracteres.</p>
            </div>
            
            <button type="submit" class="btn-submit">Atualizar Senha</button>
        </form>
        
        <div class="card-footer">
            <a href="login.jsp">Voltar para o Login</a>
        </div>
    </div>

</body>
</html>