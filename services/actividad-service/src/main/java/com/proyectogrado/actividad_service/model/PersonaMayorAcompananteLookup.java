package com.proyectogrado.actividad_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * Vista de SOLO LECTURA sobre "persona_mayor_acompanante". Esa relacion
 * la administran personamayor-service y acompanante-service;
 * actividad-service solo necesita saber que personas mayores acompaña
 * un acompañante (estado ACEPTADA) para poder mostrarle las actividades
 * de esas personas mayores.
 */
@Entity
@Table(name = "persona_mayor_acompanante")
public class PersonaMayorAcompananteLookup {

    @EmbeddedId
    private PersonaMayorAcompananteId id;

    @Column(name = "estado", nullable = false)
    private String estado;

    protected PersonaMayorAcompananteLookup() {
        // JPA
    }

    public PersonaMayorAcompananteId getId() {
        return id;
    }

    public String getEstado() {
        return estado;
    }
}
