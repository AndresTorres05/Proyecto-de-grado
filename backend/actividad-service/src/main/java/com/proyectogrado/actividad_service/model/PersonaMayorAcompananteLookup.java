package com.proyectogrado.actividad_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * Vista de solo lectura de persona_mayor_acompanante. Ese vínculo lo
 * administran persona-mayor-service y acompanante-service; aquí solo se
 * necesita saber qué personas mayores acompaña un acompañante (vínculos
 * aceptados) para mostrarle sus actividades.
 */
@Entity
@Table(name = "persona_mayor_acompanante")
public class PersonaMayorAcompananteLookup {

    @EmbeddedId
    private PersonaMayorAcompananteId id;

    @Column(name = "estado", nullable = false)
    private String estado;

    /** Lo exige JPA. */

    protected PersonaMayorAcompananteLookup() {
    }

    public PersonaMayorAcompananteId getId() {
        return id;
    }

    public String getEstado() {
        return estado;
    }
}
