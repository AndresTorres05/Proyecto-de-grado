package com.proyectogrado.actividad_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Vista de solo lectura de la tabla persona_mayor. Aquí solo sirve para
 * confirmar que el usuario autenticado (X-User-Id) es una persona mayor; su
 * id es el mismo de la tabla usuario.
 */
@Entity
@Table(name = "persona_mayor")
public class PersonaMayorLookup {

    @Id
    @Column(name = "id_usuario")
    private Integer idUsuario;

    /** Lo exige JPA. */

    protected PersonaMayorLookup() {
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }
}
