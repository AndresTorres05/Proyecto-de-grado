package com.proyectogrado.actividad_service.dto;

import java.time.LocalDate;

/**
 * Actividad propuesta por un voluntario, con su estado (PENDIENTE, ACEPTADA
 * o RECHAZADA). El voluntario ve a qué organización la presentó y la
 * organización ve qué voluntario la propuso.
 */
public record PropuestaActividadResponse(
        Integer idActividad,
        Integer idOrganizacion,
        String nombreOrganizacion,
        Integer idVoluntario,
        String nombreVoluntario,
        String estado,
        String nombre,
        String descripcion,
        LocalDate fecha,
        String hora,
        String lugar,
        String tipo,
        Integer cupos,
        String responsable
) {
}
