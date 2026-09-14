package com.proyectogrado.personas_backend.dto;

public record GustoResponse(Integer idGusto, String nombre, String categoria) {

    public GustoResponse(Integer idGusto, String nombre) {
        this(idGusto, nombre, null);
    }
}
