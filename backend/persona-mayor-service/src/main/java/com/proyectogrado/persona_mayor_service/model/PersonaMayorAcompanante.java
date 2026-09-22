package com.proyectogrado.persona_mayor_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * Relacion persona mayor <-> acompanante.
 *
 * OJO: aqui NO se referencia a PersonaMayor ni Acompanante como entidades
 * JPA (esos son de auth-backend). Solo se guardan sus IDs. Para mostrar
 * nombre/telefono en las respuestas se usa UsuarioLookup (solo lectura).
 */
@Entity
@Table(name = "persona_mayor_acompanante")
public class PersonaMayorAcompanante {

    @EmbeddedId
    private PersonaMayorAcompananteId id;

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
