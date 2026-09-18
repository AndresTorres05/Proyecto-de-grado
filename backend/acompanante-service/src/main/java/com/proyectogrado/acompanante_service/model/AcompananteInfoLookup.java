package com.proyectogrado.acompanante_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Solo lectura sobre "acompanante" (dueno de auth-backend). Aqui solo
 * se necesita leer el parentesco de OTROS acompanantes (los contactos
 * de la persona mayor que se esta consultando) -- por eso es solo
 * lectura, a diferencia de como personamayor-service escribe este mismo
 * campo cuando el propio acompanante es agregado.
 */
@Entity
@Table(name = "acompanante")
public class AcompananteInfoLookup {

    @Id
    @Column(name = "id_usuario")
    private Integer idUsuario;

    @Column(name = "parentesco")
    private String parentesco;

    protected AcompananteInfoLookup() {
        // JPA
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public String getParentesco() {
        return parentesco;
    }
}