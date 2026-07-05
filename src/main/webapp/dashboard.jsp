<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="pt-br">
<head>
    <meta charset="UTF-8">
    <title>Painel Administrativo - Gestor</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">

<nav class="navbar navbar-expand-lg navbar-dark bg-dark mb-4">
    <div class="container">
        <a class="navbar-brand" href="index.jsp">Painel do Gestor - Imobiliária</a>
        <a class="btn btn-outline-light btn-sm" href="index.jsp">Ver Site Principal</a>
    </div>
</nav>

<div class="container">
    <div class="row">
        <div class="col-md-4 mb-4">
            <div class="card shadow-sm border-0">
                <div class="card-body">
                    <h4 class="mb-4">Novo Imóvel</h4>
                    <div id="alerta-imovel" class="alert d-none p-2 small"></div>
                    <form id="form-imovel">
                        <div class="mb-2">
                            <label class="form-label small">Título do Imóvel</label>
                            <input type="text" id="titulo" class="form-control form-control-sm" required>
                        </div>
                        <div class="mb-2">
                            <label class="form-label small">Endereço Completo</label>
                            <input type="text" id="endereco" class="form-control form-control-sm" required>
                        </div>
                        <div class="mb-2">
                            <label class="form-label small">Preço de Venda (R$)</label>
                            <input type="number" step="0.01" id="preco" class="form-control form-control-sm" required>
                        </div>
                        <div class="mb-2">
                            <label class="form-label small">URL da Imagem</label>
                            <input type="url" id="imagem_url" class="form-control form-control-sm" required>
                        </div>
                        <div class="mb-3">
                            <label class="form-label small">Descrição Detalhada</label>
                            <textarea id="descricao" class="form-control form-control-sm" rows="3" required></textarea>
                        </div>
                        <button type="submit" class="btn btn-primary btn-sm w-100">Cadastrar Imóvel</button>
                    </form>
                </div>
            </div>
        </div>

        <div class="col-md-8">
            <div class="card shadow-sm border-0 p-4">
                <h4 class="mb-4">Solicitações de Visitas Agendadas</h4>
                <div class="table-responsive">
                    <table class="table table-striped table-hover align-middle small">
                        <thead class="table-dark">
                            <tr>
                                <th>Imóvel</th>
                                <th>Interessado</th>
                                <th>Data e Horário</th>
                                <th>Status</th>
                            </tr>
                        </thead>
                        <tbody id="tabela-visitas"></tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
</div>

<script>
document.addEventListener("DOMContentLoaded", () => {
    carregarVisitas();
});

function carregarVisitas() {
    fetch("api/visitas")
        .then(res => res.json())
        .then(visitas => {
            const tbody = document.getElementById("tabela-visitas");
            tbody.innerHTML = "";
            if(visitas.length === 0) {
                tbody.innerHTML = "<tr><td colspan='4' class='text-center text-muted'>Nenhuma solicitação até o momento.</td></tr>";
                return;
            }
            visitas.forEach(v => {
                tbody.innerHTML += `
                    <tr>
                        <td class="fw-bold">\${v.imovelTitulo}</td>
                        <td>\${v.usuarioNome}</td>
                        <td>\${v.dataVisita.replace("T", " ")}</td>
                        <td><span class="badge bg-warning text-dark">\${v.status}</span></td>
                    </tr>
                `;
            });
        });
}

document.getElementById("form-imovel").addEventListener("submit", (e) => {
    e.preventDefault();
    const alerta = document.getElementById("alerta-imovel");
    
    const payload = {
        titulo: document.getElementById("titulo").value,
        endereco: document.getElementById("endereco").value,
        preco: parseFloat(document.getElementById("preco").value),
        imagemUrl: document.getElementById("imagem_url").value,
        descricao: document.getElementById("descricao").value
    };

    fetch("api/imoveis", {
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
            document.getElementById("form-imovel").reset();
        }
    });
});
</script>
</body>
</html>