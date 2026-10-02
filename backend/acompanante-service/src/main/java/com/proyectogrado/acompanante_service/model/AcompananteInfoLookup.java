package com.proyectogrado.acompanante_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Vista de solo lectura de la tabla acompanante, que pertenece a
 * auth-service. Aquí solo se lee la relación de los otros acompañantes de
 * la persona mayor consultada; ese campo lo escribe persona-mayor-service
 * cuando la persona mayor agrega a un acompañante.
 */
@Entity
@Table(name = "acompanante")
public class AcompananteInfoLookup {

    @Id
    @Column(name = "id_usuario")
    private Integer idUsuario;

    @Column(name = "relacion")
    private String relacion;

    /** Lo exige JPA. */

    protected AcompananteInfoLookup() {
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public String getRelacion() {
        return relacion;
    }

    /** La escribe el acompañante cuando le envía una solicitud a una persona mayor. */
    public void setRelacion(String relacion) {
        this.relacion = relacion;
    }
}