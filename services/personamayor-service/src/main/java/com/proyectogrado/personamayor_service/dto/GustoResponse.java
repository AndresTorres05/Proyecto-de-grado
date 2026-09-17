package com.proyectogrado.personamayor_service.dto;

public record GustoResponse(Integer idGusto, String nombre, String categoria) {

    public GustoResponse(Integer idGusto, String nombre) {
        this(idGusto, nombre, null);
    }
}
