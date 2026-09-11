package com.proyectogrado.backend.dto;

public class ActualizarAcompananteRequest {

    private String nombre;
    private String correo;

    public ActualizarAcompananteRequest() {
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }
}