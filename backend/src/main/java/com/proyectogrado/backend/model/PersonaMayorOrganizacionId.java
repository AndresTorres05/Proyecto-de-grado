package com.proyectogrado.backend.model;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class PersonaMayorOrganizacionId implements Serializable {

    private Integer idPersonaMayor;
    private Integer idOrganizacion;

    public PersonaMayorOrganizacionId() {
    }

    public PersonaMayorOrganizacionId(
            Integer idPersonaMayor,
            Integer idOrganizacion
    ) {
        this.idPersonaMayor = idPersonaMayor;
        this.idOrganizacion = idOrganizacion;
    }

    public Integer getIdPersonaMayor() {
        return idPersonaMayor;
    }

    public void setIdPersonaMayor(Integer idPersonaMayor) {
        this.idPersonaMayor = idPersonaMayor;
    }

    public Integer getIdOrganizacion() {
        return idOrganizacion;
    }

    public void setIdOrganizacion(Integer idOrganizacion) {
        this.idOrganizacion = idOrganizacion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof PersonaMayorOrganizacionId)) {
            return false;
        }

        PersonaMayorOrganizacionId that =
                (PersonaMayorOrganizacionId) o;

        return Objects.equals(idPersonaMayor, that.idPersonaMayor)
                && Objects.equals(idOrganizacion, that.idOrganizacion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idPersonaMayor, idOrganizacion);
    }
}