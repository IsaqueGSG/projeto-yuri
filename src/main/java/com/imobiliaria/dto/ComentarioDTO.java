package com.imobiliaria.dto;

public class ComentarioDTO {
    private int id;
    private int idImovel;
    private int idUsuario;
    private String texto;
    private String dataComentario;
    private String usuarioNome;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getIdImovel() { return idImovel; }
    public void setIdImovel(int idImovel) { this.idImovel = idImovel; }
    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }
    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }
    public String getDataComentario() { return dataComentario; }
    public void setDataComentario(String dataComentario) { this.dataComentario = dataComentario; }
    public String getUsuarioNome() { return usuarioNome; }
    public void setUsuarioNome(String usuarioNome) { this.usuarioNome = usuarioNome; }
}