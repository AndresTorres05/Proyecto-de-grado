package com.proyectogrado.organizacion_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * Vínculo entre una persona mayor y una organización.
 *
 * No se referencian PersonaMayor ni Organizacion como entidades JPA (esas
 * tablas son de auth-service), solo sus ids. persona-mayor-service usa esta
 * misma tabla desde el otro lado (ver, aceptar o rechazar organizaciones).
 */
@Entity
@Table(name = "persona_mayor_organizacion")
public class PersonaMayorOrganizacion {

    @EmbeddedId
    private PersonaMayorOrganizacionId id;

    /** PENDIENTE hasta que la persona mayor responde; luego ACEPTADA o RECHAZADA. */
    @Column(name = "estado", nullable = false)
    private String estado = "PENDIENTE";

    public PersonaMayorOrganizacion() {
    }

    public PersonaMayorOrganizacion(Integer idPersonaMayor, Integer idOrganizacion) {
        this.id = new PersonaMayorOrganizacionId(idPersonaMayor, idOrganizacion);
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
