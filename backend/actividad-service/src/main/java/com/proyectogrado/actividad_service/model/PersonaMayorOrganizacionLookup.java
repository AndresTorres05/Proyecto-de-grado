package com.proyectogrado.actividad_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * Vista de solo lectura de persona_mayor_organizacion. Ese vínculo lo
 * administran organizacion-service y persona-mayor-service; aquí solo se
 * necesita saber con qué organizaciones tiene vínculo aceptado una persona
 * mayor, para decidir qué actividades ve y en cuáles se puede inscribir.
 */
@Entity
@Table(name = "persona_mayor_organizacion")
public class PersonaMayorOrganizacionLookup {

    @EmbeddedId
    private PersonaMayorOrganizacionId id;

    @Column(name = "estado", nullable = false)
    private String estado;

    /** Lo exige JPA. */

    protected PersonaMayorOrganizacionLookup() {
    }

    public PersonaMayorOrganizacionId getId() {
        return id;
    }

    public String getEstado() {
        return estado;
    }
}
