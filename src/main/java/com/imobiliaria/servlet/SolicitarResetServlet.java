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
import java.util.UUID;
import com.imobiliaria.util.ConexaoDB;
import com.imobiliaria.util.EmailUtil;

@WebServlet("/solicitar-reset")
public class SolicitarResetServlet extends HttpServlet {
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String email = request.getParameter("email");
        
        try (Connection conn = ConexaoDB.getConnection()) {
            // Verificar se o e-mail existe no banco
            String sqlCheck = "SELECT id FROM usuarios WHERE email = ?";
            PreparedStatement stmtCheck = conn.prepareStatement(sqlCheck);
            stmtCheck.setString(1, email);
            ResultSet rs = stmtCheck.executeQuery();
            
            if (rs.next()) {
                // Gerar token único e definir expiração (+15 minutos do horário atual)
                String token = UUID.randomUUID().toString();
                long tempoExpiracao = System.currentTimeMillis() + (15 * 60 * 1000); 
                Timestamp expiracao = new Timestamp(tempoExpiracao);
                
                // Gravar token no banco para o usuário correspondente
                String sqlUpdate = "UPDATE usuarios SET reset_token = ?, reset_token_expiracao = ? WHERE email = ?";
                PreparedStatement stmtUpdate = conn.prepareStatement(sqlUpdate);
                stmtUpdate.setString(1, token);
                stmtUpdate.setTimestamp(2, expiracao);
                stmtUpdate.setString(3, email);
                stmtUpdate.executeUpdate();
                
                // Enviar o e-mail
                EmailUtil.enviarEmailResetSenha(email, token);
            }
            
            // Por segurança, mesmo se o e-mail não existir, mostramos a mesma mensagem de sucesso
            // para evitar que invasores fiquem descobrindo e-mails válidos no sistema.
            request.setAttribute("mensagem", "Se o e-mail informado estiver cadastrado, um link de recuperação será enviado.");
            request.getRequestDispatcher("esqueci-senha.jsp").forward(request, response);
            
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("esqueci-senha.jsp?erro=Erro interno no servidor.");
        }
    }
}