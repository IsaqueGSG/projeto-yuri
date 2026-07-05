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
        <a class="navbar-brand fw-bold" href="index.jsp">Jakarta Imóveis</a>
        <a class="btn btn-outline-light btn-sm" href="index.jsp">Voltar ao Catálogo</a>
    </div>
</nav>

<div class="container">
    <div class="row">
        <div class="col-md-8">
            <img id="imovel-img" src="" class="img-fluid rounded mb-4 shadow-sm w-100" style="height: 450px; object-fit: cover; background-color: #e9ecef;">
            <div class="d-flex justify-content-between align-items-center mb-2">
                <h1 id="imovel-titulo" class="m-0 fw-bold"></h1>
                <span id="imovel-status-badge" class="badge fs-6"></span>
            </div>
            <p id="imovel-endereco" class="text-muted mb-4"></p>
            <hr>
            <h4 class="fw-semibold">Descrição do Imóvel</h4>
            <p id="imovel-descricao" class="text-secondary" style="white-space: pre-line; line-height: 1.6;"></p>

            <hr class="my-5">
            <h4 class="fw-semibold">Comentários e Feedbacks</h4>
            <% if (session.getAttribute("usuarioId") != null) { %>
                <form id="form-comentario" class="mb-4">
                    <div class="mb-3">
                        <textarea id="texto-comentario" class="form-control" rows="3" placeholder="Escreva um comentário público sobre este imóvel..." required></textarea>
                    </div>
                    <button type="submit" class="btn btn-sm btn-primary px-3 fw-semibold">Publicar Comentário</button>
                </form>
            <% } else { %>
                <p class="alert alert-warning py-2 small">Faça <a href="login.jsp" class="fw-semibold">login</a> para enviar uma avaliação sobre este imóvel.</p>
            <% } %>

            <div id="feed-comentarios" class="list-group"></div>
        </div>

        <div class="col-md-4">
            <div class="card shadow-sm border-0 p-3 sticky-top" style="top: 20px;">
                <div class="card-body">
                    <h6 class="text-muted uppercase small fw-bold">Valor do Investimento</h6>
                    <h3 id="imovel-preco" class="text-primary fw-bold mb-4"></h3>
                    
                    <hr>
                    
                    <h5 class="fw-semibold mb-3">Agendar uma Visita</h5>
                    <% if (session.getAttribute("usuarioId") != null) { %>
                        <div id="alerta-visita" class="alert d-none p-2 small text-center"></div>
                        <form id="form-visita">
                            <div class="mb-3">
                                <label class="form-label small text-muted">Selecione Data e Horário</label>
                                <input type="datetime-local" id="data-visita" class="form-control" required>
                            </div>
                            <button type="submit" id="btn-visita-submit" class="btn btn-success w-100 fw-semibold">Solicitar Agendamento</button>
                        </form>
                    <% } else { %>
                        <p class="text-muted small">Efetue a autenticação para liberar o agendamento de visitas com nossos corretores.</p>
                        <a href="login.jsp" class="btn btn-outline-dark btn-sm w-100 fw-semibold">Fazer Login</a>
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
    fetch("api/imoveis")
        .then(res => res.json())
        .then(imoveis => {
            // AJUSTE CRUCIAL: Busca o objeto certo dentro do array retornado pelo Servlet
            const imovel = imoveis.find(i => i.id == idImovel);
            
            if(!imovel) {
                alert("Imóvel não encontrado ou indisponível.");
                window.location.href = "index.jsp";
                return;
            }

            document.getElementById("imovel-img").src = imovel.imagemUrl;
            document.getElementById("imovel-titulo").textContent = imovel.titulo;
            document.getElementById("imovel-endereco").textContent = imovel.endereco;
            document.getElementById("imovel-descricao").textContent = imovel.descricao;
            document.getElementById("imovel-preco").textContent = new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(imovel.preco);
            
            // Badge de status dinâmico na tela de detalhes
            const badge = document.getElementById("imovel-status-badge");
            badge.textContent = imovel.status;
            if(imovel.status === "VENDIDO") {
                badge.className = "badge bg-danger fs-6";
                // Desabilita agendamentos se já tiver vendido
                const btnVisita = document.getElementById("btn-visita-submit");
                if(btnVisita) {
                    btnVisita.disabled = true;
                    btnVisita.textContent = "Imóvel já Vendido";
                    btnVisita.className = "btn btn-secondary w-100";
                }
            } else if(imovel.status === "ALUGADO") {
                badge.className = "badge bg-info text-dark fs-6";
            } else {
                badge.className = "badge bg-success fs-6";
            }
        }).catch(err => {
            console.error("Erro ao buscar detalhes:", err);
        });
}

function carregarComentarios() {
    fetch(`api/comentarios?idImovel=\${idImovel}`)
        .then(res => res.json())
        .then(comentarios => {
            const feed = document.getElementById("feed-comentarios");
            feed.innerHTML = "";
            if(comentarios.length === 0){
                feed.innerHTML = "<p class='text-muted small bg-white p-3 rounded text-center border'>Nenhum comentário enviado ainda. Seja o primeiro!</p>";
                return;
            }
            comentarios.forEach(c => {
                feed.innerHTML += `
                    <div class="list-group-item border-0 bg-white mb-2 shadow-sm rounded p-3">
                        <div class="d-flex w-100 justify-content-between mb-2">
                            <h6 class="mb-0 fw-bold text-dark">\${c.usuarioNome}</h6>
                            <small class="text-muted">\${c.dataComentario.substring(0,10)}</small>
                        </div>
                        <p class="mb-0 text-secondary small">\${c.texto}</p>
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
            if(dados.sucesso) {
                document.getElementById("form-visita").reset();
            }
        });
    });
}
</script>
</body>
</html>