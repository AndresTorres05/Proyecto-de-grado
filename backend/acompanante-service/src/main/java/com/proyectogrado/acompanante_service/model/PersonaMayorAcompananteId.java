package com.proyectogrado.acompanante_service.model;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class PersonaMayorAcompananteId implements Serializable {

    private Integer idPersonaMayor;
    private Integer idAcompanante;

    public PersonaMayorAcompananteId() {
    }

    public PersonaMayorAcompananteId(Integer idPersonaMayor, Integer idAcompanante) {
        this.idPersonaMayor = idPersonaMayor;
        this.idAcompanante = idAcompanante;
    }

    public Integer getIdPersonaMayor() {
        return idPersonaMayor;
    }

    public void setIdPersonaMayor(Integer idPersonaMayor) {
        this.idPersonaMayor = idPersonaMayor;
    }

    public Integer getIdAcompanante() {
        return idAcompanante;
    }

    public void setIdAcompanante(Integer idAcompanante) {
        this.idAcompanante = idAcompanante;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PersonaMayorAcompananteId that)) return false;
        return Objects.equals(idPersonaMayor, that.idPersonaMayor)
                && Objects.equals(idAcompanante, that.idAcompanante);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idPersonaMayor, idAcompanante);
    }
}
