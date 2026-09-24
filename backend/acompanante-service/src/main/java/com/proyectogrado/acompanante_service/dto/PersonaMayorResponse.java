package com.proyectogrado.acompanante_service.dto;

public record PersonaMayorResponse(
        Integer idUsuario,
        String nombre,
        String celular,
        String correo
) {
}
