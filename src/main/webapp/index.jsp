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
        <a class="navbar-brand fw-bold" href="index.jsp">Jakarta Imóveis</a>
        <div class="collapse navbar-collapse justify-content-end">
            <!-- Correção da classe de alinhamento do Bootstrap -->
            <ul class="navbar-nav align-items-center">
				<% if(session.getAttribute("usuarioNome") != null) { %>
				    <li class="nav-item"><span class="nav-link text-white me-3">Olá, <strong><%= session.getAttribute("usuarioNome") %></strong></span></li>
				    
				    <!-- ADICIONE ESTA LINHA AQUI -->
				    <li class="nav-item"><a class="btn btn-outline-info btn-sm me-2 text-white" href="cliente.jsp">Meus Agendamentos</a></li>
				
				    <% if("GESTOR".equals(session.getAttribute("usuarioRole"))) { %>
				        <li class="nav-item"><a class="btn btn-warning btn-sm me-2 fw-semibold" href="dashboard.jsp">Painel Gestor</a></li>
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
    <div class="p-5 mb-4 bg-white rounded-3 shadow-sm border">
        <h1 class="display-5 fw-bold text-dark">Encontre o imóvel dos seus sonhos</h1>
        <p class="col-md-8 fs-4 text-muted">Confira nossas ofertas exclusivas com suporte direto de gestores especializados.</p>
    </div>

    <h2 class="mb-4 fw-semibold">Imóveis Disponíveis</h2>
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
                container.innerHTML = "<div class='col-12'><p class='text-muted bg-white p-4 rounded border text-center'>Nenhum imóvel cadastrado no momento.</p></div>";
                return;
            }
            imoveis.forEach(imovel => {
                const formatado = new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(imovel.preco);
                
                // Mapeamento dinâmico de cores de status idêntico ao painel administrativo
                let badgeClass = "bg-success";
                if(imovel.status === "VENDIDO") badgeClass = "bg-danger";
                if(imovel.status === "ALUGADO") badgeClass = "bg-info text-dark";

                container.innerHTML += `
                    <div class="col-md-4 mb-4">
                        <div class="card h-100 shadow-sm border-0">
                            <img src="\${imovel.imagemUrl}" class="card-img-top" alt="Imóvel" style="height: 220px; object-fit: cover; border-top-left-radius: var(--bs-card-inner-border-radius); border-top-right-radius: var(--bs-card-inner-border-radius);">
                            <div class="card-body d-flex flex-column p-4">
                                <div class="d-flex justify-content-between align-items-start mb-2">
                                    <h5 class="card-title fw-bold text-dark m-0">\${imovel.titulo}</h5>
                                    <span class="badge \${badgeClass}">\${imovel.status}</span>
                                </div>
                                <p class="card-text text-muted small flex-grow-1">\${imovel.descricao.substring(0, 100)}...</p>
                                <h5 class="text-primary fw-bold my-3">\${formatado}</h5>
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