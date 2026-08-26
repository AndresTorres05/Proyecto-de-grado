package com.proyectogrado.backend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "persona_mayor_acompanante")
public class PersonaMayorAcompanante {

    @EmbeddedId
    private PersonaMayorAcompananteId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("idPersonaMayor")
    @JoinColumn(name = "id_persona_mayor")
    private PersonaMayor personaMayor;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("idAcompanante")
    @JoinColumn(name = "id_acompanante")
    private Acompanante acompanante;

    public PersonaMayorAcompanante() {
    }

    public PersonaMayorAcompanante(
            PersonaMayor personaMayor,
            Acompanante acompanante
    ) {
        this.personaMayor = personaMayor;
        this.acompanante = acompanante;

        this.id = new PersonaMayorAcompananteId(
                personaMayor.getIdUsuario(),
                acompanante.getIdUsuario()
        );
    }

    public PersonaMayorAcompananteId getId() {
        return id;
    }

    public void setId(PersonaMayorAcompananteId id) {
        this.id = id;
    }

    public PersonaMayor getPersonaMayor() {
        return personaMayor;
    }

    public void setPersonaMayor(PersonaMayor personaMayor) {
        this.personaMayor = personaMayor;
    }

    public Acompanante getAcompanante() {
        return acompanante;
    }

    public void setAcompanante(Acompanante acompanante) {
        this.acompanante = acompanante;
    }
}