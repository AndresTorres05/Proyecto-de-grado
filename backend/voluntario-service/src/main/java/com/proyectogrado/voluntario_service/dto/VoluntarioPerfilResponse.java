package com.proyectogrado.voluntario_service.dto;

/**
 * Perfil del voluntario: datos de su cuenta.
 */
public record VoluntarioPerfilResponse(
        Integer idUsuario,
        String nombre,
        String celular,
        String correo
) {
}
