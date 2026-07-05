package com.imobiliaria.dto;

public class VisitaDTO {
    private int id;
    private int idImovel;
    private int idUsuario;
    private String dataVisita;
    private String status;
    private String usuarioNome;
    private String imovelTitulo;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getIdImovel() { return idImovel; }
    public void setIdImovel(int idImovel) { this.idImovel = idImovel; }
    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }
    public String getDataVisita() { return dataVisita; }
    public void setDataVisita(String dataVisita) { this.dataVisita = dataVisita; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getUsuarioNome() { return usuarioNome; }
    public void setUsuarioNome(String usuarioNome) { this.usuarioNome = usuarioNome; }
    public String getImovelTitulo() { return imovelTitulo; }
    public void setImovelTitulo(String imovelTitulo) { this.imovelTitulo = imovelTitulo; }
}