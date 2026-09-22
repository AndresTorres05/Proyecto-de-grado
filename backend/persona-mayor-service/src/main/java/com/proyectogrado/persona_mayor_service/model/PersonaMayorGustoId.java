package com.proyectogrado.persona_mayor_service.model;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class PersonaMayorGustoId implements Serializable {

    private Integer idPersonaMayor;
    private Integer idGusto;

    public PersonaMayorGustoId() {
    }

    public PersonaMayorGustoId(Integer idPersonaMayor, Integer idGusto) {
        this.idPersonaMayor = idPersonaMayor;
        this.idGusto = idGusto;
    }

    public Integer getIdPersonaMayor() {
        return idPersonaMayor;
    }

    public void setIdPersonaMayor(Integer idPersonaMayor) {
        this.idPersonaMayor = idPersonaMayor;
    }

    public Integer getIdGusto() {
        return idGusto;
    }

    public void setIdGusto(Integer idGusto) {
        this.idGusto = idGusto;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PersonaMayorGustoId that)) return false;
        return Objects.equals(idPersonaMayor, that.idPersonaMayor)
                && Objects.equals(idGusto, that.idGusto);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idPersonaMayor, idGusto);
    }
}
