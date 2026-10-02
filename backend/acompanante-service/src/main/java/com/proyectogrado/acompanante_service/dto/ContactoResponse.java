package com.proyectogrado.acompanante_service.dto;

/**
 * Un acompañante de la persona mayor, con su parentesco, tal como lo ve otro
 * acompañante en la sección de contactos.
 */
public class ContactoResponse {

    private Integer idUsuario;
    private String nombre;
    private String celular;
    private String relacion;

    public ContactoResponse(Integer idUsuario, String nombre, String celular, String relacion) {
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.celular = celular;
        this.relacion = relacion;
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

    public String getRelacion() {
        return relacion;
    }
}