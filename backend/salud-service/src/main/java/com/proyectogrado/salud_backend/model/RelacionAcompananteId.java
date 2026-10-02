package com.proyectogrado.salud_backend.model;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

/**
 * Llave compuesta de persona_mayor_acompanante: persona mayor más acompañante.
 */
@Embeddable
public class RelacionAcompananteId implements Serializable {

    private Integer idPersonaMayor;
    private Integer idAcompanante;

    public RelacionAcompananteId() {
    }

    public Integer getIdPersonaMayor() {
        return idPersonaMayor;
    }

    public Integer getIdAcompanante() {
        return idAcompanante;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RelacionAcompananteId that)) return false;
        return Objects.equals(idPersonaMayor, that.idPersonaMayor)
                && Objects.equals(idAcompanante, that.idAcompanante);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idPersonaMayor, idAcompanante);
    }
}
