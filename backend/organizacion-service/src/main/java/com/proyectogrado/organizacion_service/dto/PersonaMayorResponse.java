package com.proyectogrado.organizacion_service.dto;

public record PersonaMayorResponse(
        Integer idUsuario,
        String nombre,
        String celular,
        String correo
) {
}
