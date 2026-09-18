package com.proyectogrado.auth_backend.dto;

public class LoginOtpRequest {

    private String telefono;
    private String codigo;

    public LoginOtpRequest() {
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
