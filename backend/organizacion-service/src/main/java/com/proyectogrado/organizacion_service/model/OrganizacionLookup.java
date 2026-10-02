package com.proyectogrado.organizacion_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Fila de la tabla organizacion, que crea auth-service durante el registro.
 * La dirección es un dato de perfil, así que este servicio sí puede
 * modificarla; el nombre, el correo y el celular son de la cuenta y se leen
 * con UsuarioLookup.
 */
@Entity
@Table(name = "organizacion")
public class OrganizacionLookup {

    @Id
    @Column(name = "id_organizacion")
    private Integer idOrganizacion;

    @Column(name = "direccion")
    private String direccion;

    /** Lo exige JPA. */

    protected OrganizacionLookup() {
    }

    public Integer getIdOrganizacion() {
        return idOrganizacion;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }
}