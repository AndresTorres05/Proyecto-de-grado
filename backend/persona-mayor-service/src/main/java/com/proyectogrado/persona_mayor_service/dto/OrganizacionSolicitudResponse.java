package com.proyectogrado.persona_mayor_service.dto;

public record OrganizacionSolicitudResponse(
        Integer idOrganizacion,
        String nombre,
        String celular,
        String correo
) {
}
