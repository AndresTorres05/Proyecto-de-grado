package com.proyectogrado.backend.dto;

public class AcompananteResponse {

    private Integer idUsuario;
    private String nombre;
    private String telefono;
    private String parentesco;

    public AcompananteResponse() {
    }

    public AcompananteResponse(
            Integer idUsuario,
            String nombre,
            String telefono,
            String parentesco
    ) {
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.telefono = telefono;
        this.parentesco = parentesco;
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getParentesco() {
        return parentesco;
    }

    public void setParentesco(String parentesco) {
        this.parentesco = parentesco;
    }
}