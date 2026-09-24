package com.proyectogrado.persona_mayor_service.dto;

public record AcompananteResponse(
        Integer idUsuario,
        String nombre,
        String celular,
        String parentesco
) {
}
