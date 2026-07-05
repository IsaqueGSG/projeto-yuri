package com.imobiliaria.servlet;

import com.google.gson.Gson;
import com.imobiliaria.dto.UsuarioDTO;
import com.imobiliaria.util.ConexaoDB;
import com.imobiliaria.util.EmailUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@WebServlet("/api/cadastro")
public class CadastroServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        Gson gson = new Gson();
        Map<String, Object> resposta = new HashMap<>();

        try {
            BufferedReader reader = request.getReader();
            UsuarioDTO usuario = gson.fromJson(reader, UsuarioDTO.class);

            if (usuario == null || usuario.getNome() == null || usuario.getEmail() == null || usuario.getSenha() == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resposta.put("sucesso", false);
                resposta.put("mensagem", "Dados incompletos.");
                out.print(gson.toJson(resposta));
                return;
            }

            String token = UUID.randomUUID().toString();

            try (Connection conn = ConexaoDB.getConnection()) {
                String sql = "INSERT INTO usuarios (nome, email, senha, role, verificado, token_verificacao) VALUES (?, ?, ?, 'CLIENTE', 0, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, usuario.getNome());
                    stmt.setString(2, usuario.getEmail());
                    stmt.setString(3, usuario.getSenha());
                    stmt.setString(4, token);
                    stmt.executeUpdate();
                }
            }

            try {
                EmailUtil.enviarEmailVerificacao(usuario.getEmail(), token);
            } catch (Exception e) {
                System.err.println("Erro ao disparar email, verifique credenciais do Mailtrap: " + e.getMessage());
            }

            response.setStatus(HttpServletResponse.SC_CREATED);
            resposta.put("sucesso", true);
            resposta.put("mensagem", "Cadastro realizado! Verifique seu e-mail para confirmação.");
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resposta.put("sucesso", false);
            resposta.put("mensagem", "Erro interno no servidor: " + e.getMessage());
        }
        out.print(gson.toJson(resposta));
    }
}