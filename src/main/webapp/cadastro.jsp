<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="pt-br">
<head>
    <meta charset="UTF-8">
    <title>Cadastro - Imobiliária Jakarta</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">

<div class="container mt-5">
    <div class="row justify-content-center">
        <div class="col-md-6">
            <div class="card shadow-sm border-0">
                <div class="card-body p-5">
                    <h2 class="card-title text-center mb-4">Crie sua Conta</h2>
                    <div id="alerta" class="alert d-none"></div>

                    <form id="form-cadastro">
                        <div class="mb-3">
                            <label class="form-label">Nome Completo</label>
                            <input type="text" id="nome" class="form-control" required>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">E-mail</label>
                            <input type="email" id="email" class="form-control" required>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">Senha</label>
                            <input type="password" id="senha" class="form-control" required>
                        </div>
                        <button type="submit" class="btn btn-primary w-100">Registrar Conta</button>
                    </form>
                    <div class="text-center mt-3">
                        <a href="login.jsp" class="text-decoration-none">Já possui conta? Faça o Login</a>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<script>
document.getElementById("form-cadastro").addEventListener("submit", (e) => {
    e.preventDefault();
    const alerta = document.getElementById("alerta");
    
    const payload = {
        nome: document.getElementById("nome").value,
        email: document.getElementById("email").value,
        senha: document.getElementById("senha").value
    };

    fetch("api/cadastro", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload)
    })
    .then(res => res.json())
    .then(dados => {
        alerta.className = "alert " + (dados.sucesso ? "alert-success" : "alert-danger");
        alerta.textContent = dados.mensagem;
        alerta.classList.remove("d-none");
        if(dados.sucesso) {
            document.getElementById("form-cadastro").reset();
        }
    })
    .catch(() => {
        alerta.className = "alert alert-danger";
        alerta.textContent = "Erro de conexão com o servidor.";
        alerta.classList.remove("d-none");
    });
});
</script>
</body>
</html>