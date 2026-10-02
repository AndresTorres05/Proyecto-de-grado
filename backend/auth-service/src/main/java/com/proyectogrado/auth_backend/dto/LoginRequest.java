package com.proyectogrado.auth_backend.dto;

/**
 * Datos para iniciar sesión con correo y contraseña.
 */
public class LoginRequest {

    private String correo;
    private String contrasena;

    public LoginRequest() {
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }
}