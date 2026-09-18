package com.proyectogrado.messaging_backend.dto;

public class EnviarOtpRequest {

    private String telefono;

    public EnviarOtpRequest() {
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

}