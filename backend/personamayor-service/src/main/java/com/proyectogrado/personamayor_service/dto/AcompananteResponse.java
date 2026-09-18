package com.proyectogrado.personamayor_service.dto;

public record AcompananteResponse(
        Integer idUsuario,
        String nombre,
        String telefono,
        String parentesco
) {
}
