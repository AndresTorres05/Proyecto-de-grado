package com.proyectogrado.backend.dto;

public class LoginResponse {

    private String token;
    private String rol;
    private String mensaje;
    private Integer idUsuario;

    public LoginResponse() {
    }

    public LoginResponse(String token, String rol, String mensaje) {
        this.token = token;
        this.rol = rol;
        this.mensaje = mensaje;
    }

    public LoginResponse(String token, String rol, String mensaje, Integer idUsuario) {
        this.token = token;
        this.rol = rol;
        this.mensaje = mensaje;
        this.idUsuario = idUsuario;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }
}