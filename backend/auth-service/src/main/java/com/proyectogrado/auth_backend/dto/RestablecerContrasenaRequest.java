package com.proyectogrado.auth_backend.dto;

/**
 * Datos para recuperar la contraseña con el código OTP enviado al celular.
 */
public class RestablecerContrasenaRequest {

    private String celular;
    private String codigo;
    private String contrasena;
    private String confirmarContrasena;

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public String getConfirmarContrasena() {
        return confirmarContrasena;
    }

    public void setConfirmarContrasena(String confirmarContrasena) {
        this.confirmarContrasena = confirmarContrasena;
    }
}