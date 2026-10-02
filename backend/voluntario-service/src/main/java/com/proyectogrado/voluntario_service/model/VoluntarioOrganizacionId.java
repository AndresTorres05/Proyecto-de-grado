package com.proyectogrado.voluntario_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

/**
 * Llave compuesta de voluntario_organizacion: voluntario más organización.
 */
@Embeddable
public class VoluntarioOrganizacionId implements Serializable {

    @Column(name = "id_voluntario")
    private Integer idVoluntario;

    @Column(name = "id_organizacion")
    private Integer idOrganizacion;

    public VoluntarioOrganizacionId() {
    }

    public VoluntarioOrganizacionId(Integer idVoluntario, Integer idOrganizacion) {
        this.idVoluntario = idVoluntario;
        this.idOrganizacion = idOrganizacion;
    }

    public Integer getIdVoluntario() {
        return idVoluntario;
    }

    public Integer getIdOrganizacion() {
        return idOrganizacion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VoluntarioOrganizacionId that)) return false;
        return Objects.equals(idVoluntario, that.idVoluntario)
                && Objects.equals(idOrganizacion, that.idOrganizacion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idVoluntario, idOrganizacion);
    }
}
