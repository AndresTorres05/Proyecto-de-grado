package com.proyectogrado.organizacion_service.dto;

/**
 * Cambios del perfil de la organización; por ahora solo la dirección.
 */
public class ActualizarOrganizacionRequest {

    private String direccion;

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }
}