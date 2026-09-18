package com.proyectogrado.acompanante_service.dto;

public class ContactoResponse {

    private Integer idUsuario;
    private String nombre;
    private String telefono;
    private String parentesco;

    public ContactoResponse(Integer idUsuario, String nombre, String telefono, String parentesco) {
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.telefono = telefono;
        this.parentesco = parentesco;
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getParentesco() {
        return parentesco;
    }
}