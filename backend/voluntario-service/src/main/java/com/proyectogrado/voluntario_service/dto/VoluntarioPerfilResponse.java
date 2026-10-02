package com.proyectogrado.voluntario_service.dto;

public record VoluntarioPerfilResponse(
        Integer idUsuario,
        String nombre,
        String celular,
        String correo
) {
}
