package com.proyectogrado.auth_backend.dto;

public class InformacionUsuarioResponse {

    private Integer idUsuario;
    private String nombre;
    private String celular;
    private String correo;
    private boolean tieneContrasena;

    public InformacionUsuarioResponse(Integer idUsuario, String nombre, String celular,
                                       String correo, boolean tieneContrasena) {
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.celular = celular;
        this.correo = correo;
        this.tieneContrasena = tieneContrasena;
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

    public String getCorreo() {
        return correo;
    }

    public boolean isTieneContrasena() {
        return tieneContrasena;
    }
}