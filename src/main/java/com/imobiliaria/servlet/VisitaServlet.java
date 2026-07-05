package com.imobiliaria.servlet;

import com.google.gson.Gson;
import com.imobiliaria.dto.VisitaDTO;
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

@WebServlet("/api/visitas")
public class VisitaServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        Gson gson = new Gson();

        HttpSession session = request.getSession(false);
        if (session == null || !"GESTOR".equals(session.getAttribute("usuarioRole"))) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            out.print(gson.toJson(new HashMap<>().put("erro", "Não autorizado")));
            return;
        }

        List<VisitaDTO> visitas = new ArrayList<>();
        String sql = "SELECT v.id, v.data_visita, v.status, u.nome AS usuario_nome, i.titulo AS imovel_titulo " +
                     "FROM visitas v " +
                     "JOIN usuarios u ON v.id_usuario = u.id " +
                     "JOIN imoveis i ON v.id_imovel = i.id " +
                     "ORDER BY v.data_visita ASC";

        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                VisitaDTO dto = new VisitaDTO();
                dto.setId(rs.getInt("id"));
                dto.setDataVisita(rs.getString("data_visita"));
                dto.setStatus(rs.getString("status"));
                dto.setUsuarioNome(rs.getString("usuario_nome"));
                dto.setImovelTitulo(rs.getString("imovel_titulo"));
                visitas.add(dto);
            }
            out.print(gson.toJson(visitas));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.toJson(new HashMap<>().put("erro", e.getMessage())));
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
        if (session == null || session.getAttribute("usuarioId") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resposta.put("sucesso", false);
            resposta.put("mensagem", "Autenticação requerida.");
            out.print(gson.toJson(resposta));
            return;
        }

        int idUsuario = (int) session.getAttribute("usuarioId");

        try {
            BufferedReader reader = request.getReader();
            VisitaDTO visita = gson.fromJson(reader, VisitaDTO.class);

            try (Connection conn = ConexaoDB.getConnection()) {
                String sql = "INSERT INTO visitas (id_imovel, id_usuario, data_visita, status) VALUES (?, ?, ?, 'AGENDADA')";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setInt(1, visita.getIdImovel());
                    stmt.setInt(2, idUsuario);
                    stmt.setString(3, visita.getDataVisita());
                    stmt.executeUpdate();
                }
            }

            response.setStatus(HttpServletResponse.SC_CREATED);
            resposta.put("sucesso", true);
            resposta.put("mensagem", "Visita solicitada com sucesso!");
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resposta.put("sucesso", false);
            resposta.put("mensagem", "Falha ao processar agendamento: " + e.getMessage());
        }
        out.print(gson.toJson(resposta));
    }
}