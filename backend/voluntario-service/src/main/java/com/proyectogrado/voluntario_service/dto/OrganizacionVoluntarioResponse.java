package com.proyectogrado.voluntario_service.dto;

/**
 * Organizacion vista por el voluntario. "estado" es el de su vinculo con
 * ella: PENDIENTE, ACEPTADA, RECHAZADA o null si nunca ha solicitado.
 */
public record OrganizacionVoluntarioResponse(
        Integer idOrganizacion,
        String nombre,
        String direccion,
        String celular,
        String correo,
        String estado
) {
}
