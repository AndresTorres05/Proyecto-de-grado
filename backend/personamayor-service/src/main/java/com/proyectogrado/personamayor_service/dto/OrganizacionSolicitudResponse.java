package com.proyectogrado.personamayor_service.dto;

public record OrganizacionSolicitudResponse(
        Integer idOrganizacion,
        String nombre,
        String telefono,
        String correo
) {
}
