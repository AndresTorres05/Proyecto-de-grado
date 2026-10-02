package com.proyectogrado.actividad_service.dto;

/**
 * Si la persona mayor asistió o no a la actividad.
 */
public class AsistenciaRequest {

    private Boolean asistio;

    public AsistenciaRequest() {
    }

    public Boolean getAsistio() {
        return asistio;
    }

    public void setAsistio(Boolean asistio) {
        this.asistio = asistio;
    }
}
