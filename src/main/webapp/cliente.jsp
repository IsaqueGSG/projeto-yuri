<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    // Proteção de Tela: Se não estiver logado, chuta de volta para o login
    if (session.getAttribute("usuarioId") == null) {
        response.sendRedirect("login.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html lang="pt-br">
<head>
    <meta charset="UTF-8">
    <title>Meus Agendamentos - Jakarta Imóveis</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">

<nav class="navbar navbar-expand-lg navbar-dark bg-dark mb-4">
    <div class="container">
        <a class="navbar-brand fw-bold" href="index.jsp">Jakarta Imóveis</a>
        <div class="d-flex align-items-center">
            <span class="navbar-text text-white me-3">Olá, <strong><%= session.getAttribute("usuarioNome") %></strong></span>
            <a class="btn btn-outline-light btn-sm me-2" href="index.jsp">Ver Catálogo</a>
            <a class="btn btn-outline-danger btn-sm" href="logout">Sair</a>
        </div>
    </div>
</nav>

<div class="container">
    <div class="card shadow-sm border-0 p-4">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h3 class="m-0 fw-bold text-dark">Minhas Visitas Solicitadas</h3>
            <span class="text-muted small">Acompanhe e gerencie seus horários</span>
        </div>

        <div class="table-responsive">
            <table class="table table-hover align-middle">
                <thead class="table-dark">
                    <tr>
                        <th>Imóvel</th>
                        <th>Data e Horário Agendado</th>
                        <th style="width: 150px;">Status</th>
                        <th style="width: 150px;" class="text-center">Ações</th>
                    </tr>
                </thead>
                <tbody id="tabela-meus-agendamentos">
                    <!-- Preenchido dinamicamente via AJAX -->
                </tbody>
            </table>
        </div>
    </div>
</div>

<script>
document.addEventListener("DOMContentLoaded", () => {
    carregarMeusAgendamentos();
});

function carregarMeusAgendamentos() {
    fetch("api/visitas")
        .then(res => res.json())
        .then(visitas => {
            const tbody = document.getElementById("tabela-meus-agendamentos");
            tbody.innerHTML = "";
            
            if(visitas.length === 0) {
                tbody.innerHTML = "<tr><td colspan='4' class='text-center text-muted py-4'>Você ainda não solicitou nenhuma visita.</td></tr>";
                return;
            }

            visitas.forEach(v => {
                let badgeClass = "bg-warning text-dark";
                if(v.status === "CONFIRMADA") badgeClass = "bg-success";
                if(v.status === "CANCELADA") badgeClass = "bg-danger";

                // O cliente só pode cancelar se a visita estiver PENDENTE ou CONFIRMADA
                const podeCancelar = v.status !== "CANCELADA";
                
                const botaoAcao = podeCancelar 
                    ? `<button class="btn btn-sm btn-danger px-3" onclick="cancelarMinhaVisita('\${v.id}')">Cancelar</button>`
                    : `<button class="btn btn-sm btn-light border text-muted px-3" disabled>Inativo</button>`;

                tbody.innerHTML += `
                    <tr>
                        <td class="fw-bold text-dark">\${v.imovelTitulo}</td>
                        <td class="text-secondary">\${v.dataVisita}</td>
                        <td><span class="badge \${badgeClass}">\${v.status}</span></td>
                        <td class="text-center">\${botaoAcao}</td>
                    </tr>
                `;
            });
        });
}

function cancelarMinhaVisita(id) {
    if(confirm("Deseja realmente cancelar este agendamento de visita?")) {
        fetch("api/visitas", {
            method: "PUT",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ id: parseInt(id), status: "CANCELADA" })
        })
        .then(res => res.json())
        .then(dados => {
            carregarMeusAgendamentos(); // Atualiza a tabela instantaneamente via AJAX
        });
    }
}
</script>
</body>
</html>