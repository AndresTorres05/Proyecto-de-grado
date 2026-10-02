package com.proyectogrado.persona_mayor_service.dto;

/**
 * Datos básicos de una persona mayor para las listas de vínculos.
 */
public record PersonaMayorResponse(
        Integer idUsuario,
        String nombre,
        String celular,
        String correo
) {
}
