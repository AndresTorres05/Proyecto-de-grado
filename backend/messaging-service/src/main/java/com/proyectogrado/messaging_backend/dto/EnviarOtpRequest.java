package com.proyectogrado.messaging_backend.dto;

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