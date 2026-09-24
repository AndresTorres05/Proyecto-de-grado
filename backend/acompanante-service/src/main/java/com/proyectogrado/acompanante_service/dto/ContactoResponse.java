package com.proyectogrado.acompanante_service.dto;

public class ContactoResponse {

    private Integer idUsuario;
    private String nombre;
    private String celular;
    private String parentesco;

    public ContactoResponse(Integer idUsuario, String nombre, String celular, String parentesco) {
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.celular = celular;
        this.parentesco = parentesco;
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public String getCelular() {
        return celular;
    }

    public String getParentesco() {
        return parentesco;
    }
}