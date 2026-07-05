package com.imobiliaria.dto;

public class UsuarioDTO {
    private int id;
    private String nome;
    private String email;
    private String senha;
    private String role;
    private boolean verificado;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public boolean isVerificado() { return verificado; }
    public void setVerificado(boolean verificado) { this.verificado = verificado; }
}