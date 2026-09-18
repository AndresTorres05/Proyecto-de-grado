package com.proyectogrado.salud_backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * Solo lectura sobre "persona_mayor_acompanante" (dueno de
 * personas-backend). El scheduler de recordatorios necesita saber a que
 * acompanantes ACEPTADOS avisar, y una llamada HTTP por cada medicamento
 * vencido, cada minuto, no tiene sentido -> lectura directa, igual que
 * los demas Lookup de este proyecto.
 */
@Entity
@Table(name = "persona_mayor_acompanante")
public class RelacionAcompananteLookup {

    @EmbeddedId
    private RelacionAcompananteId id;

    @Column(name = "estado")
    private String estado;

    protected RelacionAcompananteLookup() {
        // JPA
    }

    public RelacionAcompananteId getId() {
        return id;
    }

    public String getEstado() {
        return estado;
    }
}
