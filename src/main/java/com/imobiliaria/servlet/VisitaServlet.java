package com.imobiliaria.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@WebServlet("/api/visitas")
public class VisitaServlet extends HttpServlet {

    private Connection getConnection() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        return java.sql.DriverManager.getConnection("jdbc:mysql://localhost:3306/imobiliaria_db", "root", "");
    }

    // LISTAR TODAS AS VISITAS COM SOLICITANTES (GET)
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        StringBuilder json = new StringBuilder("[");
        String sql = "SELECT v.id, i.titulo AS imovelTitulo, u.nome AS usuarioNome, v.data_visita, v.status " +
                     "FROM visitas v " +
                     "INNER JOIN imoveis i ON v.imovel_id = i.id " +
                     "INNER JOIN usuarios u ON v.usuario_id = u.id " +
                     "ORDER BY v.data_visita ASC";

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                json.append(String.format(
                    "{\"id\":%d,\"imovelTitulo\":\"%s\",\"usuarioNome\":\"%s\",\"dataVisita\":\"%s\",\"status\":\"%s\"},",
                    rs.getInt("id"),
                    rs.getString("imovelTitulo").replace("\"", "\\\""),
                    rs.getString("usuarioNome").replace("\"", "\\\""),
                    rs.getTimestamp("data_visita").toString().substring(0, 16),
                    rs.getString("status")
                ));
            }
            if (json.length() > 1) json.setLength(json.length() - 1);
            json.append("]");
            out.print(json.toString());
        } catch (Exception e) {
            out.print("[]");
        }
    }

    // NOVO: SOLICITAR AGENDAMENTO DE VISITA (POST)
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            // 1. Recupera o ID do usuário logado direto da sessão do servidor
            Object usuarioIdObj = request.getSession().getAttribute("usuarioId");
            if (usuarioIdObj == null) {
                out.print("{\"sucesso\": false, \"mensagem\": \"Sessão expirada. Faça login novamente.\"}");
                return;
            }
            int usuarioId = Integer.parseInt(usuarioIdObj.toString());

            // 2. Lê o corpo JSON enviado pelo AJAX
            StringBuilder sb = new StringBuilder();
            try (BufferedReader reader = request.getReader()) {
                String linha;
                while ((linha = reader.readLine()) != null) sb.append(linha);
            }
            String json = sb.toString();

            // 3. Extrai os parâmetros do JSON usando Regex nativo
            String idImovelStr = extrairCampoJson(json, "idImovel");
            String dataVisitaStr = extrairCampoJson(json, "dataVisita"); // Formato: YYYY-MM-DDTHH:MM

            if (idImovelStr.isEmpty() || dataVisitaStr.isEmpty()) {
                out.print("{\"sucesso\": false, \"mensagem\": \"Por favor, selecione uma data válida.\"}");
                return;
            }

            int idImovel = Integer.parseInt(idImovelStr);
            // Transforma o caractere 'T' do input HTML em espaço para aceitação correta do MySQL Datetime
            String dataFormatada = dataVisitaStr.replace("T", " ") + ":00";

            // 4. Insere o registro no banco de dados com status padrão 'PENDENTE'
            try (Connection conn = getConnection();
                 PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO visitas (imovel_id, usuario_id, data_visita, status) VALUES (?, ?, ?, 'PENDENTE')")) {
                ps.setInt(1, idImovel);
                ps.setInt(2, usuarioId);
                ps.setString(3, dataFormatada);
                ps.executeUpdate();
                
                out.print("{\"sucesso\": true, \"mensagem\": \"Solicitação de visita enviada! Aguarde a confirmação do gestor.\"}");
            }
        } catch (Exception e) {
            out.print("{\"sucesso\": false, \"mensagem\": \"Erro ao agendar: " + e.getMessage() + "\"}");
        }
    }

    // ALTERAR STATUS DA VISITA (PUT)
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            StringBuilder sb = new StringBuilder();
            try (BufferedReader reader = request.getReader()) {
                String linha;
                while ((linha = reader.readLine()) != null) sb.append(linha);
            }
            
            String json = sb.toString();
            Pattern pId = Pattern.compile("\"id\"\\s*:\\s*([0-9]+)");
            Pattern pStatus = Pattern.compile("\"status\"\\s*:\\s*\"(.*?)\"");
            
            Matcher mId = pId.matcher(json);
            Matcher mStatus = pStatus.matcher(json);
            
            if (mId.find() && mStatus.find()) {
                int id = Integer.parseInt(mId.group(1));
                String status = mStatus.group(1);

                try (Connection conn = getConnection();
                     PreparedStatement ps = conn.prepareStatement("UPDATE visitas SET status=? WHERE id=?")) {
                    ps.setString(1, status);
                    ps.setInt(2, id);
                    ps.executeUpdate();
                    out.print("{\"sucesso\": true, \"mensagem\": \"Status da visita atualizado!\"}");
                }
            }
        } catch (Exception e) {
            out.print("{\"sucesso\": false, \"mensagem\": \"" + e.getMessage() + "\"}");
        }
    }

    // REMOVER VISITA DA FILA (DELETE)
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            String idParam = request.getParameter("id");
            if (idParam != null) {
                try (Connection conn = getConnection();
                     PreparedStatement ps = conn.prepareStatement("DELETE FROM visitas WHERE id=?")) {
                    ps.setInt(1, Integer.parseInt(idParam));
                    ps.executeUpdate();
                    out.print("{\"sucesso\": true, \"mensagem\": \"Agendamento removido.\"}");
                }
            }
        } catch (Exception e) {
            out.print("{\"sucesso\": false, \"mensagem\": \"" + e.getMessage() + "\"}");
        }
    }

    // Método Auxiliar para mapear JSON sem dependências de libs terceiras
    private String extrairCampoJson(String json, String campo) {
        Pattern pattern = Pattern.compile("\"" + campo + "\":\\s*\"?(.*?)\"?([,}]|$)");
        Matcher matcher = pattern.matcher(json);
        if (matcher.find()) {
            return matcher.group(1).replaceAll("^\"|\"$", "").trim();
        }
        return "";
    }
}