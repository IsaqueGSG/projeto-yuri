<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="pt-br">
<head>
    <meta charset="UTF-8">
    <title>Detalhes do Imóvel</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">

<nav class="navbar navbar-expand-lg navbar-dark bg-dark mb-4">
    <div class="container">
        <a class="navbar-brand" href="index.jsp">Jakarta Imóveis</a>
        <a class="btn btn-outline-light btn-sm" href="index.jsp">Voltar ao Catálogo</a>
    </div>
</nav>

<div class="container">
    <div class="row">
        <div class="col-md-8">
            <img id="imovel-img" src="" class="img-fluid rounded mb-4 shadow-sm w-100" style="height: 450px; object-fit: cover;">
            <h1 id="imovel-titulo" class="mb-2"></h1>
            <p id="imovel-endereco" class="text-muted mb-4"></p>
            <hr>
            <h4>Descrição do Imóvel</h4>
            <p id="imovel-descricao" class="fs-5"></p>

            <hr class="my-5">
            <h4>Comentários e Feedbacks</h4>
            <% if (session.getAttribute("usuarioId") != null) { %>
                <form id="form-comentario" class="mb-4">
                    <div class="mb-3">
                        <textarea id="texto-comentario" class="form-control" rows="3" placeholder="Escreva um comentário público sobre este imóvel..." required></textarea>
                    </div>
                    <button type="submit" class="btn btn-sm btn-primary">Publicar Comentário</button>
                </form>
            <% } else { %>
                <p class="alert alert-warning py-2">Faça <a href="login.jsp">login</a> para comentar.</p>
            <% } %>

            <div id="feed-comentarios" class="list-group"></div>
        </div>

        <div class="col-md-4">
            <div class="card shadow-sm sticky-top" style="top: 20px;">
                <div class="card-body">
                    <h5 class="text-muted">Valor do Investimento</h5>
                    <h3 id="imovel-preco" class="text-primary fw-bold mb-4"></h3>
                    
                    <hr>
                    
                    <h5>Agendar uma Visita</h5>
                    <% if (session.getAttribute("usuarioId") != null) { %>
                        <div id="alerta-visita" class="alert d-none p-2 small"></div>
                        <form id="form-visita">
                            <div class="mb-3">
                                <label class="form-label small">Selecione Data e Horário</label>
                                <input type="datetime-local" id="data-visita" class="form-control" required>
                            </div>
                            <button type="submit" class="btn btn-success w-100">Solicitar Agendamento</button>
                        </form>
                    <% } else { %>
                        <p class="text-muted small">Efetue a autenticação para realizar agendamentos.</p>
                        <a href="login.jsp" class="btn btn-outline-dark btn-sm w-100">Fazer Login</a>
                    <% } %>
                </div>
            </div>
        </div>
    </div>
</div>

<script>
const params = new URLSearchParams(window.location.search);
const idImovel = params.get("id");

if(!idImovel) {
    window.location.href = "index.jsp";
}

document.addEventListener("DOMContentLoaded", () => {
    carregarDetalhes();
    carregarComentarios();
});

function carregarDetalhes() {
    fetch(`api/imoveis?id=\${idImovel}`)
        .then(res => res.json())
        .then(imovel => {
            document.getElementById("imovel-img").src = imovel.imagemUrl;
            document.getElementById("imovel-titulo").textContent = imovel.titulo;
            document.getElementById("imovel-endereco").textContent = imovel.endereco;
            document.getElementById("imovel-descricao").textContent = imovel.descricao;
            document.getElementById("imovel-preco").textContent = new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(imovel.preco);
        });
}

function carregarComentarios() {
    fetch(`api/comentarios?idImovel=\${idImovel}`)
        .then(res => res.json())
        .then(comentarios => {
            const feed = document.getElementById("feed-comentarios");
            feed.innerHTML = "";
            if(comentarios.length === 0){
                feed.innerHTML = "<p class='text-muted small'>Nenhum comentário enviado ainda.</p>";
                return;
            }
            comentarios.forEach(c => {
                feed.innerHTML += `
                    <div class="list-group-item border-0 bg-white mb-2 shadow-sm rounded">
                        <div class="d-flex w-100 justify-content-between">
                            <h6 class="mb-1 fw-bold">\${c.usuarioNome}</h6>
                            <small class="text-muted">\${c.dataComentario.substring(0,10)}</small>
                        </div>
                        <p class="mb-1 small">\${c.texto}</p>
                    </div>
                `;
            });
        });
}

if(document.getElementById("form-comentario")) {
    document.getElementById("form-comentario").addEventListener("submit", (e) => {
        e.preventDefault();
        const payload = {
            idImovel: parseInt(idImovel),
            texto: document.getElementById("texto-comentario").value
        };

        fetch("api/comentarios", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(payload)
        }).then(() => {
            document.getElementById("texto-comentario").value = "";
            carregarComentarios();
        });
    });
}

if(document.getElementById("form-visita")) {
    document.getElementById("form-visita").addEventListener("submit", (e) => {
        e.preventDefault();
        const alerta = document.getElementById("alerta-visita");
        const payload = {
            idImovel: parseInt(idImovel),
            dataVisita: document.getElementById("data-visita").value
        };

        fetch("api/visitas", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(payload)
        })
        .then(res => res.json())
        .then(dados => {
            alerta.className = "alert " + (dados.sucesso ? "alert-success" : "alert-danger");
            alerta.textContent = dados.mensagem;
            alerta.classList.remove("d-none");
        });
    });
}
</script>
</body>
</html>