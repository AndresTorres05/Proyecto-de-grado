package com.proyectogrado.personas_backend.dto;

public record PersonaMayorResponse(
        Integer idUsuario,
        String nombre,
        String telefono,
        String correo
) {
}
