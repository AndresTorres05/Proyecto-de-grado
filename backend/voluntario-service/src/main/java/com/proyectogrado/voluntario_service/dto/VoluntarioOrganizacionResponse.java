package com.proyectogrado.voluntario_service.dto;

/** Voluntario visto por la organización (vinculado o con solicitud pendiente). */
public record VoluntarioOrganizacionResponse(
        Integer idUsuario,
        String nombre,
        String celular,
        String correo
) {
}
