package com.proyectogrado.persona_mayor_service.dto;

/**
 * Gusto tal como lo recibe el frontend.
 */
public record GustoResponse(Integer idGusto, String nombre, String categoria) {

    /** Sin categoría: se usa al listar los gustos de una persona mayor. */
    public GustoResponse(Integer idGusto, String nombre) {
        this(idGusto, nombre, null);
    }
}
