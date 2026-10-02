package com.proyectogrado.auth_backend.dto;

/**
 * Datos para iniciar sesión con el celular y el código OTP.
 */
public class LoginOtpRequest {

    private String celular;
    private String codigo;

    public LoginOtpRequest() {
    }

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
}
