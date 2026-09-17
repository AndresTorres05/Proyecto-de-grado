package com.proyectogrado.personamayor_service.model;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

@Entity
@Table(name = "persona_mayor_gusto")
public class PersonaMayorGusto {

    @EmbeddedId
    private PersonaMayorGustoId id;

    // Gusto SI es dueno de este servicio, por eso aqui si se referencia
    // como entidad JPA completa (a diferencia de PersonaMayor/Acompanante).
    @ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
    @MapsId("idGusto")
    @JoinColumn(name = "id_gusto")
    private Gusto gusto;

    public PersonaMayorGusto() {
    }

    public PersonaMayorGusto(Integer idPersonaMayor, Gusto gusto) {
        this.gusto = gusto;
        this.id = new PersonaMayorGustoId(idPersonaMayor, gusto.getIdGusto());
    }

    public PersonaMayorGustoId getId() {
        return id;
    }

    public void setId(PersonaMayorGustoId id) {
        this.id = id;
    }

    public Gusto getGusto() {
        return gusto;
    }

    public void setGusto(Gusto gusto) {
        this.gusto = gusto;
    }
}
