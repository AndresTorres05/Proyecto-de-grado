package com.proyectogrado.actividad_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Vista de SOLO LECTURA sobre "persona_mayor" (tabla de
 * auth-backend/personamayor-service). Aqui solo se usa para confirmar
 * que el X-User-Id autenticado corresponde a una persona mayor; el
 * id_usuario es el mismo id que usa "usuario" y "actividad"/"participacion".
 */
@Entity
@Table(name = "persona_mayor")
public class PersonaMayorLookup {

    @Id
    @Column(name = "id_usuario")
    private Integer idUsuario;

    protected PersonaMayorLookup() {
        // JPA
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }
}
