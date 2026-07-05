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
        <!-- Coluna Esquerda: Cadastro/Edição de Imóveis -->
        <div class="col-md-4 mb-4">
            <div class="card shadow-sm border-0 sticky-top" style="top: 20px;">
                <div class="card-body">
                    <h4 id="titulo-form" class="mb-4">Novo Imóvel</h4>
                    <div id="alerta-imovel" class="alert d-none p-2 small"></div>
                    
                    <form id="form-imovel">
                        <input type="hidden" id="imovel-id" value="">
                        
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
                        
                        <button type="submit" id="btn-submit-imovel" class="btn btn-primary btn-sm w-100">Cadastrar Imóvel</button>
                        <button type="button" id="btn-cancelar-edicao" class="btn btn-secondary btn-sm w-100 mt-2 d-none" onclick="cancelarEdicao()">Cancelar Edição</button>
                    </form>
                </div>
            </div>
        </div>

        <!-- Coluna Direita: Listagens -->
        <div class="col-md-8">
            <!-- Tabela de Solicitações de Visitas -->
            <div class="card shadow-sm border-0 p-4 mb-4">
                <h4 class="mb-4">Solicitações de Visitas Agendadas</h4>
                <div class="table-responsive">
                    <table class="table table-striped table-hover align-middle small">
                        <thead class="table-dark">
                            <tr>
                                <th>Imóvel</th>
                                <th>Interessado</th>
                                <th>Data e Horário</th>
                                <th>Status</th>
                                <th style="width: 110px;">Ações</th>
                            </tr>
                        </thead>
                        <tbody id="tabela-visitas"></tbody>
                    </table>
                </div>
            </div>

            <!-- Tabela de Imóveis Cadastrados -->
            <div class="card shadow-sm border-0 p-4">
                <h4 class="mb-4">Imóveis sob Gestão</h4>
                <div class="table-responsive">
                    <table class="table table-striped table-hover align-middle small">
                        <thead class="table-dark">
                            <tr>
                                <th style="width: 70px;">Miniatura</th>
                                <th>Título do Imóvel</th>
                                <th>Preço de Venda</th>
                                <th>Status</th>
                                <th style="width: 140px;">Ações</th>
                            </tr>
                        </thead>
                        <tbody id="tabela-imoveis"></tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

<script>
document.addEventListener("DOMContentLoaded", () => {
    carregarVisitas();
    carregarImoveis();
    configurarGatilhoEdicao();
});

function carregarVisitas() {
    fetch("api/visitas")
        .then(res => res.json())
        .then(visitas => {
            const tbody = document.getElementById("tabela-visitas");
            tbody.innerHTML = "";
            if(visitas.length === 0) {
                tbody.innerHTML = "<tr><td colspan='5' class='text-center text-muted'>Nenhuma solicitação até o momento.</td></tr>";
                return;
            }
            visitas.forEach(v => {
                let badgeClass = "bg-warning text-dark";
                if(v.status === "CONFIRMADA") badgeClass = "bg-success";
                if(v.status === "CANCELADA") badgeClass = "bg-danger";

                tbody.innerHTML += `
                    <tr>
                        <td class="fw-bold">\${v.imovelTitulo}</td>
                        <td>\${v.usuarioNome}</td>
                        <td>\${v.dataVisita}</td>
                        <td><span class="badge \${badgeClass}">\${v.status}</span></td>
                        <td>
                            <div class="dropdown">
                                <button class="btn btn-light btn-sm dropdown-toggle border" type="button" data-bs-toggle="dropdown">
                                    Gerenciar
                                </button>
                                <ul class="dropdown-menu dropdown-menu-end small">
                                    <li><a class="dropdown-item text-success fw-semibold" href="#" onclick="alterarStatusVisita('\${v.id}', 'CONFIRMADA'); return false;">✓ Confirmar</a></li>
                                    <li><a class="dropdown-item text-danger fw-semibold" href="#" onclick="alterarStatusVisita('\${v.id}', 'CANCELADA'); return false;">✗ Cancelar</a></li>
                                    <li><hr class="dropdown-divider"></li>
                                    <li><a class="dropdown-item text-muted" href="#" onclick="excluirVisita('\${v.id}'); return false;">Remover Registro</a></li>
                                </ul>
                            </div>
                        </td>
                    </tr>
                `;
            });
        });
}

function carregarImoveis() {
    fetch("api/imoveis")
        .then(res => res.json())
        .then(imoveis => {
            const tbody = document.getElementById("tabela-imoveis");
            tbody.innerHTML = "";
            if(imoveis.length === 0) {
                tbody.innerHTML = "<tr><td colspan='5' class='text-center text-muted'>Nenhum imóvel cadastrado.</td></tr>";
                return;
            }
            imoveis.forEach(i => {
                const precoFormatado = new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(i.preco);
                let badgeClass = "bg-success";
                if(i.status === "VENDIDO") badgeClass = "bg-danger";

                tbody.innerHTML += `
                    <tr>
                        <td><img src="\${i.imagemUrl}" style="width:60px; height:45px; object-fit:cover; border-radius:4px;"></td>
                        <td>
                            <div class="fw-bold">\${i.titulo}</div>
                            <small class="text-muted">\${i.endereco}</small>
                        </td>
                        <td class="text-primary fw-bold">\${precoFormatado}</td>
                        <td><span class="badge \${badgeClass}">\${i.status}</span></td>
                        <td>
                            <div class="d-flex gap-1">
                                <button class="btn btn-sm btn-outline-warning btn-editar-imovel" 
                                    data-id="\${i.id}" data-titulo="\${i.titulo}" data-endereco="\${i.endereco}" 
                                    data-preco="\${i.preco}" data-imagem="\${i.imagemUrl}" data-descricao="\${i.descricao}">
                                    Editar
                                </button>
                                <button class="btn btn-sm btn-outline-danger" onclick="excluirImovel('\${i.id}')">Excluir</button>
                            </div>
                        </td>
                    </tr>
                `;
            });
        });
}

function configurarGatilhoEdicao() {
    document.getElementById("tabela-imoveis").addEventListener("click", (e) => {
        if(e.target.classList.contains("btn-editar-imovel")) {
            const btn = e.target;
            document.getElementById("imovel-id").value = btn.dataset.id;
            document.getElementById("titulo").value = btn.dataset.titulo;
            document.getElementById("endereco").value = btn.dataset.endereco;
            document.getElementById("preco").value = btn.dataset.preco;
            document.getElementById("imagem_url").value = btn.dataset.imagem;
            document.getElementById("descricao").value = btn.dataset.descricao;

            document.getElementById("titulo-form").textContent = "Editar Imóvel";
            document.getElementById("btn-submit-imovel").textContent = "Salvar Alterações";
            document.getElementById("btn-cancelar-edicao").classList.remove("d-none");
            window.scrollTo({ top: 0, behavior: 'smooth' });
        }
    });
}

function cancelarEdicao() {
    document.getElementById("form-imovel").reset();
    document.getElementById("imovel-id").value = "";
    document.getElementById("titulo-form").textContent = "Novo Imóvel";
    document.getElementById("btn-submit-imovel").textContent = "Cadastrar Imóvel";
    document.getElementById("btn-cancelar-edicao").classList.add("d-none");
}

document.getElementById("form-imovel").addEventListener("submit", (e) => {
    e.preventDefault();
    const idExistente = document.getElementById("imovel-id").value;
    
    const payload = {
        titulo: document.getElementById("titulo").value,
        endereco: document.getElementById("endereco").value,
        preco: parseFloat(document.getElementById("preco").value),
        imagemUrl: document.getElementById("imagem_url").value,
        descricao: document.getElementById("descricao").value
    };

    const metodo = idExistente ? "PUT" : "POST";
    if (idExistente) payload.id = idExistente;

    fetch("api/imoveis", {
        method: metodo,
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload)
    })
    .then(res => res.json())
    .then(dados => {
        const alerta = document.getElementById("alerta-imovel");
        alerta.className = "alert " + (dados.sucesso ? "alert-success" : "alert-danger");
        alerta.textContent = dados.mensagem;
        alerta.classList.remove("d-none");
        if(dados.sucesso) {
            cancelarEdicao();
            carregarImoveis();
        }
    });
});

function excluirImovel(id) {
    if(confirm("Excluir este imóvel permanentemente do banco?")) {
        fetch(`api/imoveis?id=\${id}`, { method: "DELETE" })
        .then(res => res.json())
        .then(() => carregarImoveis());
    }
}

function alterarStatusVisita(id, novoStatus) {
    fetch("api/visitas", {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ id: parseInt(id), status: novoStatus })
    })
    .then(() => carregarVisitas());
}

function excluirVisita(id) {
    if(confirm("Remover este agendamento permanentemente?")) {
        fetch(`api/visitas?id=\${id}`, { method: "DELETE" })
        .then(() => carregarVisitas());
    }
}
</script>
</body>
</html>