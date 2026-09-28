package com.proyectogrado.salud_backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "persona_mayor_organizacion")
public class PersonaMayorOrganizacion {

    @EmbeddedId
    private PersonaMayorOrganizacionId id;

    @Column(name = "estado", nullable = false)
    private String estado;

    public PersonaMayorOrganizacion() {
    }

    public PersonaMayorOrganizacion(
            Integer idPersonaMayor,
            Integer idOrganizacion
    ) {
        this.id = new PersonaMayorOrganizacionId(
                idPersonaMayor,
                idOrganizacion
        );
    }

    public PersonaMayorOrganizacionId getId() {
        return id;
    }

    public void setId(PersonaMayorOrganizacionId id) {
        this.id = id;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}