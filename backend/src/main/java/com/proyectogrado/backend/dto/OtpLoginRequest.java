package com.proyectogrado.backend.dto;

public class OtpLoginRequest {

    private String telefono;
    private String codigo;

    public OtpLoginRequest() {
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }
}