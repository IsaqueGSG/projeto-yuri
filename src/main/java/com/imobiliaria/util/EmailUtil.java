package com.imobiliaria.util;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailUtil {
    private static final String HOST = "sandbox.smtp.mailtrap.io";
    private static final String PORT = "2525";
    private static final String USERNAME = "SEU_USER_MAILTRAP"; 
    private static final String PASSWORD = "SEU_PASSWORD_MAILTRAP";

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
}