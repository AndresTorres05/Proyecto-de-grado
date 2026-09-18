package com.proyectogrado.actividad_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Vista de SOLO LECTURA sobre "acompanante" (tabla de
 * auth-backend/personamayor-service). Aqui solo se usa para confirmar
 * que el X-User-Id autenticado corresponde a un acompañante.
 */
@Entity
@Table(name = "acompanante")
public class AcompananteLookup {

    @Id
    @Column(name = "id_usuario")
    private Integer idUsuario;

    protected AcompananteLookup() {
        // JPA
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }
}
