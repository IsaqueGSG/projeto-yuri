<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="pt-br">
<head>
    <meta charset="UTF-8">
    <title>Imobiliária Jakarta</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">

<nav class="navbar navbar-expand-lg navbar-dark bg-dark mb-4">
    <div class="container">
        <a class="navbar-brand" href="index.jsp">Jakarta Imóveis</a>
        <div class="collapse navbar-collapse justify-content-end">
            <ul class="navbar-row navbar-nav align-items-center">
                <% if(session.getAttribute("usuarioNome") != null) { %>
                    <li class="nav-item"><span class="nav-link text-white me-3">Olá, <%= session.getAttribute("usuarioNome") %></span></li>
                    <% if("GESTOR".equals(session.getAttribute("usuarioRole"))) { %>
                        <li class="nav-item"><a class="btn btn-warning btn-sm me-2" href="dashboard.jsp">Painel Gestor</a></li>
                    <% } %>
                    <li class="nav-item"><a class="btn btn-outline-danger btn-sm" href="logout">Sair</a></li>
                <% } else { %>
                    <li class="nav-item"><a class="btn btn-outline-light btn-sm me-2" href="login.jsp">Login</a></li>
                    <li class="nav-item"><a class="btn btn-primary btn-sm" href="cadastro.jsp">Cadastrar</a></li>
                <% } %>
            </ul>
        </div>
    </div>
</nav>

<div class="container">
    <div class="p-5 mb-4 bg-white rounded-3 shadow-sm">
        <h1 class="display-5 fw-bold">Encontre o imóvel dos seus sonhos</h1>
        <p class="col-md-8 fs-4">Confira nossas ofertas exclusivas com suporte direto de gestores especializados.</p>
    </div>

    <h2 class="mb-4">Imóveis Disponíveis</h2>
    <div id="container-imoveis" class="row"></div>
</div>

<script>
document.addEventListener("DOMContentLoaded", () => {
    fetch("api/imoveis")
        .then(res => res.json())
        .then(imoveis => {
            const container = document.getElementById("container-imoveis");
            container.innerHTML = "";
            if(imoveis.length === 0) {
                container.innerHTML = "<p class='text-muted'>Nenhum imóvel cadastrado no momento.</p>";
                return;
            }
            imoveis.forEach(imovel => {
                const formatado = new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(imovel.preco);
                container.innerHTML += `
                    <div class="col-md-4 mb-4">
                        <div class="card h-100 shadow-sm">
                            <img src="\${imovel.imagemUrl}" class="card-img-top" alt="Imóvel" style="height: 220px; object-fit: cover;">
                            <div class="card-body d-flex flex-column">
                                <h5 class="card-title">\${imovel.titulo}</h5>
                                <p class="card-text text-muted flex-grow-1">\${imovel.descricao.substring(0, 100)}...</p>
                                <h6 class="text-primary mb-3">\${formatado}</h6>
                                <span class="badge bg-success mb-3 align-self-start">\${imovel.status}</span>
                                <a href="detalhes.jsp?id=\${imovel.id}" class="btn btn-outline-primary w-100 mt-auto">Ver Detalhes</a>
                            </div>
                        </div>
                    </div>
                `;
            });
        });
});
</script>
</body>
</html>