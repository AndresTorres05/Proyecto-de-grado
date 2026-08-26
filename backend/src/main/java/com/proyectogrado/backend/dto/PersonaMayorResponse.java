package com.proyectogrado.backend.dto;

public class PersonaMayorResponse {

    private Integer idUsuario;
    private String nombre;
    private String telefono;

    public PersonaMayorResponse() {
    }

    public PersonaMayorResponse(
            Integer idUsuario,
            String nombre,
            String telefono
    ) {
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.telefono = telefono;
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
}
