package com.proyectogrado.actividad_service.model;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

/**
 * Llave compuesta de participacion: persona mayor más actividad.
 */
@Embeddable
public class ParticipacionId implements Serializable {

    private Integer idPersonaMayor;
    private Integer idActividad;

    public ParticipacionId() {
    }

    public ParticipacionId(Integer idPersonaMayor, Integer idActividad) {
        this.idPersonaMayor = idPersonaMayor;
        this.idActividad = idActividad;
    }

    public Integer getIdPersonaMayor() {
        return idPersonaMayor;
    }

    public void setIdPersonaMayor(Integer idPersonaMayor) {
        this.idPersonaMayor = idPersonaMayor;
    }

    public Integer getIdActividad() {
        return idActividad;
    }

    public void setIdActividad(Integer idActividad) {
        this.idActividad = idActividad;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ParticipacionId that)) return false;
        return Objects.equals(idPersonaMayor, that.idPersonaMayor)
                && Objects.equals(idActividad, that.idActividad);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idPersonaMayor, idActividad);
    }
}
