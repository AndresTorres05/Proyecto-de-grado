package com.proyectogrado.backend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "persona_mayor_gusto")
public class PersonaMayorGusto {

    @EmbeddedId
    private PersonaMayorGustoId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("idPersonaMayor")
    @JoinColumn(name = "id_persona_mayor")
    private PersonaMayor personaMayor;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("idGusto")
    @JoinColumn(name = "id_gusto")
    private Gusto gusto;

    public PersonaMayorGusto() {
    }

    public PersonaMayorGusto(PersonaMayor personaMayor, Gusto gusto) {
        this.personaMayor = personaMayor;
        this.gusto = gusto;
        this.id = new PersonaMayorGustoId(personaMayor.getIdUsuario(), gusto.getIdGusto());
    }

    public PersonaMayorGustoId getId() {
        return id;
    }

    public void setId(PersonaMayorGustoId id) {
        this.id = id;
    }

    public PersonaMayor getPersonaMayor() {
        return personaMayor;
    }

    public void setPersonaMayor(PersonaMayor personaMayor) {
        this.personaMayor = personaMayor;
    }

    public Gusto getGusto() {
        return gusto;
    }

    public void setGusto(Gusto gusto) {
        this.gusto = gusto;
    }
}
