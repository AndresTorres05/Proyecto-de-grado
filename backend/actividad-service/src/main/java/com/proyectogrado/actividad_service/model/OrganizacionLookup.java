package com.proyectogrado.actividad_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Vista de solo lectura de la tabla organizacion, que pertenece a
 * auth-service. Sirve para mostrarle al voluntario el nombre de la
 * organización a la que presentó cada propuesta.
 */
@Entity
@Table(name = "organizacion")
public class OrganizacionLookup {

    @Id
    @Column(name = "id_organizacion")
    private Integer idOrganizacion;

    @Column(name = "nombre")
    private String nombre;

    /** Lo exige JPA. */
    protected OrganizacionLookup() {
    }

    public Integer getIdOrganizacion() {
        return idOrganizacion;
    }

    public String getNombre() {
        return nombre;
    }
}
