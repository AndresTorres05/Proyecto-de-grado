package com.proyectogrado.backend.dto;

public class LoginResponse {

    private String token;
    private String rol;
    private String mensaje;

    public LoginResponse() {
    }

    public LoginResponse(String token, String rol, String mensaje) {
        this.token = token;
        this.rol = rol;
        this.mensaje = mensaje;
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
}