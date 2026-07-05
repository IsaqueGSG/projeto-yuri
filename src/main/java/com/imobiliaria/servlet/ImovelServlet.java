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

@WebServlet("/api/imoveis")
public class ImovelServlet extends HttpServlet {

    private Connection getConnection() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
        return java.sql.DriverManager.getConnection("jdbc:mysql://localhost:3306/imobiliaria_db", "root", "");
    }

    // LISTAR IMÓVEIS (GET) - CORRIGIDO COM LOCALE US
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        StringBuilder json = new StringBuilder("[");
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM imoveis ORDER BY id DESC");
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                // Forçamos o java.util.Locale.US para que o preço use PONTO (.) e não VÍRGULA (,)
                json.append(String.format(java.util.Locale.US,
                    "{\"id\":%d,\"titulo\":\"%s\",\"endereco\":\"%s\",\"preco\":%.2f,\"imagemUrl\":\"%s\",\"descricao\":\"%s\",\"status\":\"%s\"},",
                    rs.getInt("id"),
                    rs.getString("titulo").replace("\"", "\\\""),
                    rs.getString("endereco").replace("\"", "\\\""),
                    rs.getDouble("preco"),
                    rs.getString("imagem_url").replace("\"", "\\\""),
                    // Tratamos quebras de linha que também invalidam o JSON (\r e \n)
                    rs.getString("descricao").replace("\"", "\\\"").replace("\r", "").replace("\n", "\\n"),
                    rs.getString("status")
                ));
            }
            if (json.length() > 1) json.setLength(json.length() - 1); // Remove a última vírgula
            json.append("]");
            out.print(json.toString());
        } catch (Exception e) {
            out.print("[]");
        }
    }
    
    // CADASTRAR IMÓVEL (POST)
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            String body = lerCorpoRequisicao(request);
            String titulo = extrairCampoJson(body, "titulo");
            String endereco = extrairCampoJson(body, "endereco");
            double preco = Double.parseDouble(extrairCampoJson(body, "preco"));
            String imagemUrl = extrairCampoJson(body, "imagemUrl");
            String descricao = extrairCampoJson(body, "descricao");

            try (Connection conn = getConnection();
                 PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO imoveis (titulo, endereco, preco, imagem_url, descricao, status) VALUES (?, ?, ?, ?, ?, 'DISPONÍVEL')")) {
                ps.setString(1, titulo);
                ps.setString(2, endereco);
                ps.setDouble(3, preco);
                ps.setString(4, imagemUrl);
                ps.setString(5, descricao);
                ps.executeUpdate();
                out.print("{\"sucesso\": true, \"mensagem\": \"Imóvel cadastrado com sucesso!\"}");
            }
        } catch (Exception e) {
            out.print("{\"sucesso\": false, \"mensagem\": \"Erro ao salvar: " + e.getMessage() + "\"}");
        }
    }

    // EDITAR IMÓVEL (PUT)
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            String body = lerCorpoRequisicao(request);
            int id = Integer.parseInt(extrairCampoJson(body, "id"));
            String titulo = extrairCampoJson(body, "titulo");
            String endereco = extrairCampoJson(body, "endereco");
            double preco = Double.parseDouble(extrairCampoJson(body, "preco"));
            String imagemUrl = extrairCampoJson(body, "imagemUrl");
            String descricao = extrairCampoJson(body, "descricao");

            try (Connection conn = getConnection();
                 PreparedStatement ps = conn.prepareStatement(
                     "UPDATE imoveis SET titulo=?, endereco=?, preco=?, imagem_url=?, descricao=? WHERE id=?")) {
                ps.setString(1, titulo);
                ps.setString(2, endereco);
                ps.setDouble(3, preco);
                ps.setString(4, imagemUrl);
                ps.setString(5, descricao);
                ps.setInt(6, id);
                ps.executeUpdate();
                out.print("{\"sucesso\": true, \"mensagem\": \"Imóvel atualizado com sucesso!\"}");
            }
        } catch (Exception e) {
            out.print("{\"sucesso\": false, \"mensagem\": \"Erro ao atualizar: " + e.getMessage() + "\"}");
        }
    }

    // EXCLUIR IMÓVEL (DELETE)
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            String idParam = request.getParameter("id");
            if (idParam != null) {
                try (Connection conn = getConnection();
                     PreparedStatement ps = conn.prepareStatement("DELETE FROM imoveis WHERE id=?")) {
                    ps.setInt(1, Integer.parseInt(idParam));
                    ps.executeUpdate();
                    out.print("{\"sucesso\": true, \"mensagem\": \"Imóvel removido do banco!\"}");
                }
            }
        } catch (Exception e) {
            out.print("{\"sucesso\": false, \"mensagem\": \"Erro ao excluir: " + e.getMessage() + "\"}");
        }
    }

    // Métodos Utilitários nativos para processar JSON
    private String lerCorpoRequisicao(HttpServletRequest request) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String linha;
            while ((linha = reader.readLine()) != null) sb.append(linha);
        }
        return sb.toString();
    }

    private String extrairCampoJson(String json, String campo) {
        Pattern pattern = Pattern.compile("\"" + campo + "\":\\s*\"?(.*?)\"?([,}]|$)");
        Matcher matcher = pattern.matcher(json);
        if (matcher.find()) {
            return matcher.group(1).replaceAll("^\"|\"$", "").trim();
        }
        return "";
    }
}