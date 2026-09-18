package com.proyectogrado.actividad_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * Inscripcion de una persona mayor en una actividad.
 *
 * OJO: no se referencia PersonaMayor como entidad JPA (esa tabla es de
 * auth-backend/personamayor-service), solo su id dentro de
 * ParticipacionId. Actividad si es local a este servicio, pero se
 * maneja igual por id para mantener el mismo estilo simple que el
 * resto de tablas de relacion en la arquitectura (ver
 * PersonaMayorOrganizacion, PersonaMayorAcompanante).
 */
@Entity
@Table(name = "participacion")
public class Participacion {

    @EmbeddedId
    private ParticipacionId id;

    @Column(name = "asistio")
    private Boolean asistio;

    public Participacion() {
    }

    public Participacion(Integer idPersonaMayor, Integer idActividad) {
        this.id = new ParticipacionId(idPersonaMayor, idActividad);
    }

    public ParticipacionId getId() {
        return id;
    }

    public void setId(ParticipacionId id) {
        this.id = id;
    }

    public Boolean getAsistio() {
        return asistio;
    }

    public void setAsistio(Boolean asistio) {
        this.asistio = asistio;
    }
}
