package com.imobiliaria.util;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailUtil {
    private static final String HOST = "sandbox.smtp.mailtrap.io";
    private static final String PORT = "2525";
    
    private static final String USERNAME = "f92759b82b653b"; 
    private static final String PASSWORD = "f6c461163a5527";

    public static void enviarEmailVerificacao(String destinatario, String token) throws MessagingException {
        Properties prop = new Properties();
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.starttls.enable", "true");
        prop.put("mail.smtp.host", HOST);
        prop.put("mail.smtp.port", PORT);
        prop.put("mail.smtp.ssl.trust", HOST);

        Session session = Session.getInstance(prop, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(USERNAME, PASSWORD);
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress("noreply@sistemaimobiliaria.com"));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
        message.setSubject("Ativação de Conta - Sistema de Imobiliária");

        String linkAtivacao = "http://localhost:8080/verificar-email?token=" + token;
        
        String corpoHtml = "<h1>Bem-vindo à nossa Imobiliária!</h1>"
                + "<p>Para confirmar seu cadastro e ativar sua conta, clique no link abaixo:</p>"
                + "<p><a href='" + linkAtivacao + "' target='_blank'>Ativar Minha Conta</a></p>"
                + "<br><small>Se você não realizou este cadastro, desconsidere este e-mail.</small>";

        message.setContent(corpoHtml, "text/html; charset=UTF-8");

        Transport.send(message);
    }

    public static void enviarEmailResetSenha(String emailDestino, String token) {
        String linkReset = "http://localhost:8080/redefinir-senha?token=" + token;

        String assunto = "Recuperação de Senha - Sistema Imobiliária";
        String conteudo = "<h3>Redefinição de Senha</h3>"
                + "<p>Você solicitou a alteração da sua senha. Clique no link abaixo para criar uma nova senha:</p>"
                + "<p><a href='" + linkReset + "'>Clique aqui para redefinir sua senha</a></p>"
                + "<br><p>Se você não solicitou esta alteração, ignore este e-mail. O link expira em 15 minutos.</p>";

        Properties props = new Properties();
        // A LINHA props.build(); FOI REMOVIDA DAQUI
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", HOST);
        props.put("mail.smtp.port", PORT);
        props.put("mail.smtp.ssl.trust", HOST);

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(USERNAME, PASSWORD);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("no-reply@imobiliaria.com"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(emailDestino));
            message.setSubject(assunto);
            message.setContent(conteudo, "text/html; charset=UTF-8");
            Transport.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Erro ao enviar e-mail de redefinição", e);
        }
    }
}