package com.proyectogrado.voluntario_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Vista de SOLO LECTURA sobre la tabla "voluntario", que es dueño de
 * auth-backend (nace con el registro). Sirve para confirmar que el
 * usuario autenticado realmente es un voluntario.
 */
@Entity
@Table(name = "voluntario")
public class VoluntarioLookup {

    @Id
    @Column(name = "id_usuario")
    private Integer idUsuario;

    protected VoluntarioLookup() {
        // JPA
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }
}
