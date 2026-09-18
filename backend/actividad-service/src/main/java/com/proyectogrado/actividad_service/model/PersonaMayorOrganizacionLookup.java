package com.proyectogrado.actividad_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * Vista de SOLO LECTURA sobre "persona_mayor_organizacion". Esa relacion
 * la administran organizacion-service (lado organizacion) y
 * personamayor-service (lado persona mayor); actividad-service solo
 * necesita saber que organizaciones tiene ACEPTADAS una persona mayor
 * para decidir que actividades puede ver/en cuales puede inscribirse,
 * o para verificar antes de dejarla inscribirse en una actividad.
 */
@Entity
@Table(name = "persona_mayor_organizacion")
public class PersonaMayorOrganizacionLookup {

    @EmbeddedId
    private PersonaMayorOrganizacionId id;

    @Column(name = "estado", nullable = false)
    private String estado;

    protected PersonaMayorOrganizacionLookup() {
        // JPA
    }

    public PersonaMayorOrganizacionId getId() {
        return id;
    }

    public String getEstado() {
        return estado;
    }
}
