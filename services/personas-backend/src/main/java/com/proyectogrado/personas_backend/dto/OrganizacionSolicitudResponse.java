package com.proyectogrado.personas_backend.dto;

public record OrganizacionSolicitudResponse(
        Integer idOrganizacion,
        String nombre,
        String telefono,
        String correo
) {
}
