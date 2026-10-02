package com.proyectogrado.messaging_backend.dto;

/**
 * Cuerpo para pedir un código OTP. Hoy no se usa: OtpController recibe el
 * cuerpo como Map con la clave "phoneNumber".
 */
public class EnviarOtpRequest {

    private String celular;

    public EnviarOtpRequest() {
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

}