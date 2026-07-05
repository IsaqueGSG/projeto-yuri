package com.imobiliaria.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter(urlPatterns = {"/dashboard.jsp"})
public class AuthFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(false);

        boolean logado = (session != null && session.getAttribute("usuarioRole") != null);
        
        if (logado) {
            String role = (String) session.getAttribute("usuarioRole");
            if ("GESTOR".equals(role)) {
                chain.doFilter(request, response);
                return;
            }
        }
        
        httpResponse.sendRedirect(httpRequest.getContextPath() + "/login.jsp?erro=acesso-negado");
    }
}