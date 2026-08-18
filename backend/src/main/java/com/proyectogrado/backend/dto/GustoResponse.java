package com.proyectogrado.backend.dto;

public class GustoResponse {

    private Integer idGusto;
    private String nombre;

    public GustoResponse() {
    }

    public GustoResponse(Integer idGusto, String nombre) {
        this.idGusto = idGusto;
        this.nombre = nombre;
    }

    public Integer getIdGusto() {
        return idGusto;
    }

    public void setIdGusto(Integer idGusto) {
        this.idGusto = idGusto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
