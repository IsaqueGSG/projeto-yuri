package com.imobiliaria.servlet;

import com.google.gson.Gson;
import com.imobiliaria.dto.ComentarioDTO;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/api/comentarios")
public class ComentarioServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        Gson gson = new Gson();

        String idImovelParam = request.getParameter("idImovel");
        if (idImovelParam == null || idImovelParam.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        int idImovel = Integer.parseInt(idImovelParam);
        List<ComentarioDTO> comentarios = new ArrayList<>();

        String sql = "SELECT c.id, c.texto, c.data_comentario, u.nome AS usuario_nome " +
                     "FROM comentarios c " +
                     "JOIN usuarios u ON c.id_usuario = u.id " +
                     "WHERE c.id_imovel = ? ORDER BY c.data_comentario DESC";

        try (Connection conn = ConexaoDB.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idImovel);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ComentarioDTO dto = new ComentarioDTO();
                    dto.setId(rs.getInt("id"));
                    dto.setTexto(rs.getString("texto"));
                    dto.setDataComentario(rs.getString("data_comentario"));
                    dto.setUsuarioNome(rs.getString("usuario_nome"));
                    comentarios.add(dto);
                }
            }
            out.print(gson.toJson(comentarios));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.toJson(new ArrayList<>()));
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
            resposta.put("mensagem", "Autenticação requerida para comentar.");
            out.print(gson.toJson(resposta));
            return;
        }

        int idUsuario = (int) session.getAttribute("usuarioId");

        try {
            BufferedReader reader = request.getReader();
            ComentarioDTO comentario = gson.fromJson(reader, ComentarioDTO.class);

            try (Connection conn = ConexaoDB.getConnection()) {
                String sql = "INSERT INTO comentarios (id_imovel, id_usuario, texto, data_comentario) VALUES (?, ?, ?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setInt(1, comentario.getIdImovel());
                    stmt.setInt(2, idUsuario);
                    stmt.setString(3, comentario.getTexto());
                    stmt.setString(4, LocalDateTime.now().toString());
                    stmt.executeUpdate();
                }
            }

            response.setStatus(HttpServletResponse.SC_CREATED);
            resposta.put("sucesso", true);
            resposta.put("mensagem", "Comentário enviado.");
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resposta.put("sucesso", false);
            resposta.put("mensagem", "Erro ao salvar comentário: " + e.getMessage());
        }
        out.print(gson.toJson(resposta));
    }
}