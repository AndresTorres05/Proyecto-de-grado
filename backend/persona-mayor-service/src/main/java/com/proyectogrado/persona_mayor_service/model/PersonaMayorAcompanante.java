package com.proyectogrado.persona_mayor_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * Vínculo entre una persona mayor y un acompañante.
 *
 * No se referencia a PersonaMayor ni a Acompanante como entidades JPA,
 * porque esas tablas son de auth-service: solo se guardan los ids. Para
 * mostrar nombre y celular en las respuestas se usa UsuarioLookup.
 */
@Entity
@Table(name = "persona_mayor_acompanante")
public class PersonaMayorAcompanante {

    @EmbeddedId
    private PersonaMayorAcompananteId id;

    /** PENDIENTE hasta que el acompañante responde; luego ACEPTADA o RECHAZADA. */
    @Column(name = "estado", nullable = false)
    private String estado = "PENDIENTE";

    public PersonaMayorAcompanante() {
    }

    public PersonaMayorAcompanante(Integer idPersonaMayor, Integer idAcompanante) {
        this.id = new PersonaMayorAcompananteId(idPersonaMayor, idAcompanante);
    }

    public PersonaMayorAcompananteId getId() {
        return id;
    }

    public void setId(PersonaMayorAcompananteId id) {
        this.id = id;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
