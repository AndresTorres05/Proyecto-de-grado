package com.proyectogrado.backend.dto;

public class AcompanantePerfilResponse {

    private Integer idUsuario;
    private String nombre;
    private String telefono;
    private String correo;
    private boolean tieneContrasena;

    public AcompanantePerfilResponse() {
    }

    public AcompanantePerfilResponse(
            Integer idUsuario,
            String nombre,
            String telefono,
            String correo,
            boolean tieneContrasena
    ) {
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.telefono = telefono;
        this.correo = correo;
        this.tieneContrasena = tieneContrasena;
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

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public boolean isTieneContrasena() {
        return tieneContrasena;
    }

    public void setTieneContrasena(boolean tieneContrasena) {
        this.tieneContrasena = tieneContrasena;
    }
}