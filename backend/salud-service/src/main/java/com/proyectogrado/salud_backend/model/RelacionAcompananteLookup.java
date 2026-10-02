package com.proyectogrado.salud_backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * Vista de solo lectura de persona_mayor_acompanante. El scheduler de
 * recordatorios necesita saber a qué acompañantes aceptados avisar, y
 * hacer una llamada HTTP por cada medicamento, cada minuto, no tiene
 * sentido: se lee la tabla directamente, como en los demás Lookup del
 * proyecto.
 */
@Entity
@Table(name = "persona_mayor_acompanante")
public class RelacionAcompananteLookup {

    @EmbeddedId
    private RelacionAcompananteId id;

    /** PENDIENTE, ACEPTADA o RECHAZADA. */
    @Column(name = "estado")
    private String estado;

    /** Lo exige JPA. */
    protected RelacionAcompananteLookup() {
    }

    public RelacionAcompananteId getId() {
        return id;
    }

    public String getEstado() {
        return estado;
    }
}
