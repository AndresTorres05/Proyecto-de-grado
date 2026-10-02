package com.proyectogrado.auth_backend.dto;

/**
 * Datos para cambiar la contraseña desde "Mi información".
 */
public class CambiarContrasenaRequest {

    /** Solo se exige si el usuario ya tenía contraseña. */
    private String contrasenaActual;
    private String nuevaContrasena;

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