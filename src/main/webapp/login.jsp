<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="pt-br">
<head>
    <meta charset="UTF-8">
    <title>Login - Imobiliária Jakarta</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">

<div class="container mt-5">
    <div class="row justify-content-center">
        <div class="col-md-5">
            <div class="card shadow-sm border-0">
                <div class="card-body p-5">
                    <h2 class="card-title text-center mb-4">Autenticação</h2>
                    <div id="alerta" class="alert d-none"></div>

                    <form id="form-login">
                        <div class="mb-3">
                            <label class="form-label">E-mail</label>
                            <input type="email" id="email" class="form-control" required>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">Senha</label>
                            <input type="password" id="senha" class="form-control" required>
                        </div>
                        <button type="submit" class="btn btn-dark w-100">Entrar</button>
                    </form>
                    <div class="text-center mt-3">
                        <a href="cadastro.jsp" class="text-decoration-none">Não tem conta? Cadastre-se</a>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<script>
document.addEventListener("DOMContentLoaded", () => {
    const params = new URLSearchParams(window.location.search);
    const alerta = document.getElementById("alerta");
    
    if(params.has("sucesso") && params.get("sucesso") === "email-verificado") {
        alerta.className = "alert alert-success";
        alerta.textContent = "Sua conta foi ativada com sucesso! Proceda com o login.";
        alerta.classList.remove("d-none");
    } else if(params.has("erro")) {
        alerta.className = "alert alert-danger";
        alerta.textContent = "Acesso negado ou token inválido.";
        alerta.classList.remove("d-none");
    }
});

document.getElementById("form-login").addEventListener("submit", (e) => {
    e.preventDefault();
    const alerta = document.getElementById("alerta");
    
    const payload = {
        email: document.getElementById("email").value,
        senha: document.getElementById("senha").value
    };

    fetch("api/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload)
    })
    .then(res => {
        if (!res.ok) {
            return res.json().then(err => { throw new Error(err.mensagem) });
        }
        return res.json();
    })
    .then(dados => {
        if(dados.role === "GESTOR") {
            window.location.href = "dashboard.jsp";
        } else {
            window.location.href = "index.jsp";
        }
    })
    .catch(err => {
        alerta.className = "alert alert-danger";
        alerta.textContent = err.message || "Credenciais inválidas.";
        alerta.classList.remove("d-none");
    });
});
</script>
</body>
</html>