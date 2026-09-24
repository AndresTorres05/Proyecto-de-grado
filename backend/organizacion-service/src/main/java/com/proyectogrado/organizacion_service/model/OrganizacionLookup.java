package com.proyectogrado.organizacion_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * "Organizacion" (id_organizacion + direccion) nace en auth-backend
 * junto con el Usuario, en la misma transaccion del registro. Pero
 * "direccion" es un dato de perfil propio, no de identidad -> este
 * servicio SI puede escribirlo, a diferencia de nombre/correo/celular
 * (esos viven en UsuarioLookup, 100% solo lectura).
 */
@Entity
@Table(name = "organizacion")
public class OrganizacionLookup {

    @Id
    @Column(name = "id_organizacion")
    private Integer idOrganizacion;

    @Column(name = "direccion")
    private String direccion;

    protected OrganizacionLookup() {
        // JPA
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