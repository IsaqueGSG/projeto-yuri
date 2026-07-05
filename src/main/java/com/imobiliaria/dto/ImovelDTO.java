package com.imobiliaria.dto;

import java.math.BigDecimal;

public class ImovelDTO {
    private int id;
    private String titulo;
    private String descricao;
    private BigDecimal preco;
    private String endereco;
    private String imagemUrl;
    private String status;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public BigDecimal getPreco() { return preco; }
    public void setPreco(BigDecimal preco) { this.preco = preco; }
    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }
    public String getImagemUrl() { return imagemUrl; }
    public void setImagemUrl(String imagemUrl) { this.imagemUrl = imagemUrl; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}