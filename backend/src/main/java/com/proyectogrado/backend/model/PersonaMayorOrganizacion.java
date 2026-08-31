package com.proyectogrado.backend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "persona_mayor_organizacion")
public class PersonaMayorOrganizacion {

    @EmbeddedId
    private PersonaMayorOrganizacionId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("idPersonaMayor")
    @JoinColumn(name = "id_persona_mayor")
    private PersonaMayor personaMayor;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("idOrganizacion")
    @JoinColumn(name = "id_organizacion")
    private Organizacion organizacion;

    public PersonaMayorOrganizacion() {
    }

    public PersonaMayorOrganizacion(
            PersonaMayor personaMayor,
            Organizacion organizacion
    ) {
        this.personaMayor = personaMayor;
        this.organizacion = organizacion;

        this.id = new PersonaMayorOrganizacionId(
                personaMayor.getIdUsuario(),
                organizacion.getIdOrganizacion()
        );
    }

    public PersonaMayorOrganizacionId getId() {
        return id;
    }

    public void setId(PersonaMayorOrganizacionId id) {
        this.id = id;
    }

    public PersonaMayor getPersonaMayor() {
        return personaMayor;
    }

    public void setPersonaMayor(PersonaMayor personaMayor) {
        this.personaMayor = personaMayor;
    }

    public Organizacion getOrganizacion() {
        return organizacion;
    }

    public void setOrganizacion(Organizacion organizacion) {
        this.organizacion = organizacion;
    }
}