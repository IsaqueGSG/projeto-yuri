package com.imobiliaria.servlet;

import com.imobiliaria.util.ConexaoDB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;

@WebServlet("/verificar-email")
public class VerificarEmailServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String token = request.getParameter("token");

        if (token == null || token.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?erro=token-invalido");
            return;
        }

        try (Connection conn = ConexaoDB.getConnection()) {
            String sql = "UPDATE usuarios SET verificado = 1, token_verificacao = NULL WHERE token_verificacao = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, token);
                int linhasAfetadas = stmt.executeUpdate();

                if (linhasAfetadas > 0) {
                    response.sendRedirect(request.getContextPath() + "/login.jsp?sucesso=email-verificado");
                } else {
                    response.sendRedirect(request.getContextPath() + "/login.jsp?erro=token-inexistente");
                }
            }
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/login.jsp?erro=erro-banco");
        }
    }
}