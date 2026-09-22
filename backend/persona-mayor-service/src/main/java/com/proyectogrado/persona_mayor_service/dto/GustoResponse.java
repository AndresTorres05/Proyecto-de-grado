package com.proyectogrado.persona_mayor_service.dto;

public record GustoResponse(Integer idGusto, String nombre, String categoria) {

    public GustoResponse(Integer idGusto, String nombre) {
        this(idGusto, nombre, null);
    }
}
