package com.proyectogrado.actividad_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

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

    // Fecha/hora de inicio de la actividad para la que ya se envio el
    // recordatorio de 1 hora antes. Si la organizacion cambia la hora,
    // deja de coincidir y se vuelve a avisar para la nueva.
    @Column(name = "recordatorio_enviado_para")
    private LocalDateTime recordatorioEnviadoPara;

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

    public LocalDateTime getRecordatorioEnviadoPara() {
        return recordatorioEnviadoPara;
    }

    public void setRecordatorioEnviadoPara(LocalDateTime recordatorioEnviadoPara) {
        this.recordatorioEnviadoPara = recordatorioEnviadoPara;
    }
}
