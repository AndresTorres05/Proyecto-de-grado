package com.proyectogrado.actividad_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/**
 * Inscripción de una persona mayor en una actividad.
 *
 * No se referencia PersonaMayor como entidad JPA (esa tabla es de
 * auth-service): solo su id, dentro de ParticipacionId. Actividad sí es de
 * este servicio, pero también se maneja por id, igual que las demás tablas
 * de vínculos del proyecto.
 */
@Entity
@Table(name = "participacion")
public class Participacion {

    @EmbeddedId
    private ParticipacionId id;

    /** null hasta que la organización registra la asistencia. */
    @Column(name = "asistio")
    private Boolean asistio;

    /**
     * Inicio de la actividad para el que ya se envió el recordatorio de
     * 1 hora antes. Si la organización cambia la hora, deja de coincidir y
     * se vuelve a avisar para la nueva.
     */
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
