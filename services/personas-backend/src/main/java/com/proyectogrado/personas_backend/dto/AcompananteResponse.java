package com.proyectogrado.personas_backend.dto;

public record AcompananteResponse(
        Integer idUsuario,
        String nombre,
        String telefono,
        String parentesco
) {
}
