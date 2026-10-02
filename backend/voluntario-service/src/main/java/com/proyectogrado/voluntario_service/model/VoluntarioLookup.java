package com.proyectogrado.voluntario_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Vista de solo lectura de la tabla voluntario, que crea auth-service
 * durante el registro. Sirve para confirmar que el usuario autenticado
 * realmente es un voluntario.
 */
@Entity
@Table(name = "voluntario")
public class VoluntarioLookup {

    @Id
    @Column(name = "id_usuario")
    private Integer idUsuario;

    /** Lo exige JPA. */
    protected VoluntarioLookup() {
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }
}
