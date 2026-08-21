package com.proyectogrado.backend.dto;

public class GustoResponse {

    private Integer idGusto;
    private String nombre;
    private String categoria;

    public GustoResponse() {
    }

    public GustoResponse(Integer idGusto, String nombre) {
        this(idGusto, nombre, null);
    }

    public GustoResponse(Integer idGusto, String nombre, String categoria) {
        this.idGusto = idGusto;
        this.nombre = nombre;
        this.categoria = categoria;
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

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }
}