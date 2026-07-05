package com.imobiliaria.servlet;

import com.google.gson.Gson;
import com.imobiliaria.dto.ImovelDTO;
import com.imobiliaria.util.ConexaoDB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/api/imoveis")
public class ImovelServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        Gson gson = new Gson();

        String idParam = request.getParameter("id");

        try (Connection conn = ConexaoDB.getConnection()) {
            if (idParam != null && !idParam.trim().isEmpty()) {
                int id = Integer.parseInt(idParam);
                String sql = "SELECT * FROM imoveis WHERE id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setInt(1, id);
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next()) {
                            ImovelDTO imovel = new ImovelDTO();
                            imovel.setId(rs.getInt("id"));
                            imovel.setTitulo(rs.getString("titulo"));
                            imovel.setDescricao(rs.getString("descricao"));
                            imovel.setPreco(rs.getBigDecimal("preco"));
                            imovel.setEndereco(rs.getString("endereco"));
                            imovel.setImagemUrl(rs.getString("imagem_url"));
                            imovel.setStatus(rs.getString("status"));
                            out.print(gson.toJson(imovel));
                        } else {
                            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                            Map<String, String> err = new HashMap<>();
                            err.put("erro", "Imóvel não localizado.");
                            out.print(gson.toJson(err));
                        }
                    }
                }
            } else {
                List<ImovelDTO> lista = new ArrayList<>();
                String sql = "SELECT * FROM imoveis ORDER BY id DESC";
                try (PreparedStatement stmt = conn.prepareStatement(sql);
                     ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        ImovelDTO imovel = new ImovelDTO();
                        imovel.setId(rs.getInt("id"));
                        imovel.setTitulo(rs.getString("titulo"));
                        imovel.setDescricao(rs.getString("descricao"));
                        imovel.setPreco(rs.getBigDecimal("preco"));
                        imovel.setEndereco(rs.getString("endereco"));
                        imovel.setImagemUrl(rs.getString("imagem_url"));
                        imovel.setStatus(rs.getString("status"));
                        lista.add(imovel);
                    }
                }
                out.print(gson.toJson(lista));
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            Map<String, String> err = new HashMap<>();
            err.put("erro", e.getMessage());
            out.print(gson.toJson(err));
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        Gson gson = new Gson();
        Map<String, Object> resposta = new HashMap<>();

        HttpSession session = request.getSession(false);
        if (session == null || !"GESTOR".equals(session.getAttribute("usuarioRole"))) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            resposta.put("sucesso", false);
            resposta.put("mensagem", "Acesso não autorizado.");
            out.print(gson.toJson(resposta));
            return;
        }

        try {
            BufferedReader reader = request.getReader();
            ImovelDTO imovel = gson.fromJson(reader, ImovelDTO.class);

            try (Connection conn = ConexaoDB.getConnection()) {
                String sql = "INSERT INTO imoveis (titulo, descricao, preco, endereco, imagem_url, status) VALUES (?, ?, ?, ?, ?, 'DISPONIVEL')";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, imovel.getTitulo());
                    stmt.setString(2, imovel.getDescricao());
                    stmt.setBigDecimal(3, imovel.getPreco());
                    stmt.setString(4, imovel.getEndereco());
                    stmt.setString(5, imovel.getImagemUrl());
                    stmt.executeUpdate();
                }
            }

            response.setStatus(HttpServletResponse.SC_CREATED);
            resposta.put("sucesso", true);
            resposta.put("mensagem", "Imóvel cadastrado com sucesso.");
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resposta.put("sucesso", false);
            resposta.put("mensagem", "Erro no servidor: " + e.getMessage());
        }
        out.print(gson.toJson(resposta));
    }
}