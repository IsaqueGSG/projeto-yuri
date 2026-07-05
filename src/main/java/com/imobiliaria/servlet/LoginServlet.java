package com.imobiliaria.servlet;

import com.google.gson.Gson;
import com.imobiliaria.dto.LoginDTO;
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
import java.util.HashMap;
import java.util.Map;

@WebServlet("/api/login")
public class LoginServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        Gson gson = new Gson();
        Map<String, Object> resposta = new HashMap<>();

        try {
            BufferedReader reader = request.getReader();
            LoginDTO login = gson.fromJson(reader, LoginDTO.class);

            if (login == null || login.getEmail() == null || login.getSenha() == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resposta.put("sucesso", false);
                resposta.put("mensagem", "E-mail e senha são obrigatórios.");
                out.print(gson.toJson(resposta));
                return;
            }

            try (Connection conn = ConexaoDB.getConnection()) {
                String sql = "SELECT id, nome, role, verificado FROM usuarios WHERE email = ? AND senha = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, login.getEmail());
                    stmt.setString(2, login.getSenha());
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next()) {
                            boolean verificado = rs.getBoolean("verificado");
                            if (!verificado) {
                                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                                resposta.put("sucesso", false);
                                resposta.put("mensagem", "Conta pendente de ativação via e-mail.");
                            } else {
                                HttpSession session = request.getSession(true);
                                session.setAttribute("usuarioId", rs.getInt("id"));
                                session.setAttribute("usuarioNome", rs.getString("nome"));
                                session.setAttribute("usuarioRole", rs.getString("role"));

                                response.setStatus(HttpServletResponse.SC_OK);
                                resposta.put("sucesso", true);
                                resposta.put("role", rs.getString("role"));
                                resposta.put("mensagem", "Autenticação bem-sucedida.");
                            }
                        } else {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            resposta.put("sucesso", false);
                            resposta.put("mensagem", "Usuário ou senha incorretos.");
                        }
                    }
                }
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resposta.put("sucesso", false);
            resposta.put("mensagem", "Erro no processamento: " + e.getMessage());
        }
        out.print(gson.toJson(resposta));
    }
}