package com.proyectogrado.persona_mayor_service.dto;

public record PersonaMayorResponse(
        Integer idUsuario,
        String nombre,
        String telefono,
        String correo
) {
}
