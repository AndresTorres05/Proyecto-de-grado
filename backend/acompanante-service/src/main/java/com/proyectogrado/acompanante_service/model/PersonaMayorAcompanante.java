package com.proyectogrado.acompanante_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * Vínculo entre una persona mayor y un acompañante.
 *
 * Solo se guardan los ids, porque las tablas persona_mayor y acompanante son
 * de auth-service. Para mostrar nombre y celular se usa UsuarioLookup.
 *
 * persona-mayor-service usa esta misma tabla desde el otro lado (agregar y
 * listar acompañantes); aquí se lee y se cambia el estado al aceptar o
 * rechazar solicitudes.
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
