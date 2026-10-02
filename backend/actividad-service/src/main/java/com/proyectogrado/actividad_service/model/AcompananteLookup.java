package com.proyectogrado.actividad_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Vista de solo lectura de la tabla acompanante. Aquí solo sirve para
 * confirmar que el usuario autenticado (X-User-Id) es un acompañante.
 */
@Entity
@Table(name = "acompanante")
public class AcompananteLookup {

    @Id
    @Column(name = "id_usuario")
    private Integer idUsuario;

    /** Lo exige JPA. */

    protected AcompananteLookup() {
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }
}
