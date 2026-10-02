package com.proyectogrado.persona_mayor_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Fila de la tabla acompanante, que crea auth-service durante el registro.
 * Aquí solo se usa la columna relacion, que describe el vínculo con la
 * persona mayor (por ejemplo, "hijo" o "vecina") y no la identidad del
 * acompañante; por eso este servicio sí puede escribirla, a diferencia de
 * UsuarioLookup, que es solo de lectura.
 */
@Entity
@Table(name = "acompanante")
public class AcompananteLookup {

    @Id
    @Column(name = "id_usuario")
    private Integer idUsuario;

    @Column(name = "relacion")
    private String relacion;

    /** Lo exige JPA. */
    protected AcompananteLookup() {
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public String getRelacion() {
        return relacion;
    }

    public void setRelacion(String relacion) {
        this.relacion = relacion;
    }
}
