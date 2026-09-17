package com.proyectogrado.organizacion_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * Relacion persona mayor <-> organizacion.
 *
 * Igual que en acompanante-service: no se referencian PersonaMayor ni
 * Organizacion como entidades JPA (esos son de auth-backend), solo sus
 * IDs. Esta misma tabla también la usa personamayor-service desde el
 * otro lado de la relación (ver/aceptar/rechazar organizaciones).
 */
@Entity
@Table(name = "persona_mayor_organizacion")
public class PersonaMayorOrganizacion {

    @EmbeddedId
    private PersonaMayorOrganizacionId id;

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
