package com.proyectogrado.messaging_backend.dto;

/**
 * Celular y código para verificar un OTP. Hoy no se usa: OtpController
 * recibe el cuerpo como Map con las claves "phoneNumber" y "code".
 */
public class OtpLoginRequest {

    private String celular;
    private String codigo;

    public OtpLoginRequest() {
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