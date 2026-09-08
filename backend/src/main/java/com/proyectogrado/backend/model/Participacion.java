package com.proyectogrado.backend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "participacion")
public class Participacion {

    @EmbeddedId
    private ParticipacionId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("idPersonaMayor")
    @JoinColumn(name = "id_persona_mayor")
    private PersonaMayor personaMayor;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("idActividad")
    @JoinColumn(name = "id_actividad")
    private Actividad actividad;

    @Column(name = "asistio")
    private Boolean asistio;

    public Participacion() {
    }

    public Participacion(PersonaMayor personaMayor, Actividad actividad) {
        this.personaMayor = personaMayor;
        this.actividad = actividad;
        this.id = new ParticipacionId(personaMayor.getIdUsuario(), actividad.getIdActividad());
    }

    public ParticipacionId getId() {
        return id;
    }

    public void setId(ParticipacionId id) {
        this.id = id;
    }

    public PersonaMayor getPersonaMayor() {
        return personaMayor;
    }

    public void setPersonaMayor(PersonaMayor personaMayor) {
        this.personaMayor = personaMayor;
    }

    public Actividad getActividad() {
        return actividad;
    }

    public void setActividad(Actividad actividad) {
        this.actividad = actividad;
    }

    public Boolean getAsistio() {
        return asistio;
    }

    public void setAsistio(Boolean asistio) {
        this.asistio = asistio;
    }
}