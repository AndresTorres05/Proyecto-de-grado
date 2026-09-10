package com.proyectogrado.backend.dto;

public class CambiarContrasenaRequest {

    private String contrasenaActual;
    private String nuevaContrasena;

    public CambiarContrasenaRequest() {}

    public String getContrasenaActual() {
        return contrasenaActual;
    }

    public void setContrasenaActual(String contrasenaActual) {
        this.contrasenaActual = contrasenaActual;
    }

    public String getNuevaContrasena() {
        return nuevaContrasena;
    }

    public void setNuevaContrasena(String nuevaContrasena) {
        this.nuevaContrasena = nuevaContrasena;
    }
}