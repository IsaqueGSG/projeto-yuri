package com.imobiliaria.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import com.imobiliaria.util.ConexaoDB;

@WebServlet("/redefinir-senha")
public class RedefinirSenhaServlet extends HttpServlet {

    // GET: Valida o token vindo da URL
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String token = request.getParameter("token");

        if (token == null || token.isEmpty()) {
            response.sendRedirect("login.jsp?erro=Token inválido.");
            return;
        }

        try (Connection conn = ConexaoDB.getConnection()) {
            String sql = "SELECT id, reset_token_expiracao FROM usuarios WHERE reset_token = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, token);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Timestamp expiracao = rs.getTimestamp("reset_token_expiracao");
                // Verifica se o token já passou da data de expiração
                if (expiracao != null && expiracao.after(new Timestamp(System.currentTimeMillis()))) {
                    // Token válido! Encaminha para a tela de preenchimento da nova senha
                    request.setAttribute("token", token);
                    request.getRequestDispatcher("redefinir-senha.jsp").forward(request, response);
                    return;
                }
            }
            // Se chegou aqui, o token é falso ou expirou
            response.sendRedirect("login.jsp?erro=O link de recuperação expirou ou é inválido.");

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("login.jsp?erro=Erro ao validar o token.");
        }
    }

    // POST: Processa a alteração de senha definitiva
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String token = request.getParameter("token");
        String novaSenha = request.getParameter("senha");

        try (Connection conn = ConexaoDB.getConnection()) {
            // Atualiza a senha e apaga o token para que não possa ser reutilizado
            String sql = "UPDATE usuarios SET senha = ?, reset_token = NULL, reset_token_expiracao = NULL "
                       + "WHERE reset_token = ? AND reset_token_expiracao > NOW()";
            
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, novaSenha); // Nota: Se usar criptografia/Hash, aplique-a aqui!
            stmt.setString(2, token);
            
            int linhasAfetadas = stmt.executeUpdate();

            if (linhasAfetadas > 0) {
                response.sendRedirect("login.jsp?sucesso=Senha alterada com sucesso! Faça login.");
            } else {
                response.sendRedirect("login.jsp?erro=Não foi possível redefinir a senha. Tente novamente.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("login.jsp?erro=Erro ao atualizar a senha.");
        }
    }
}