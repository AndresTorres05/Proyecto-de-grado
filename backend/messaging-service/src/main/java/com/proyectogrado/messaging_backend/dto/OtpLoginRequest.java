package com.proyectogrado.messaging_backend.dto;

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