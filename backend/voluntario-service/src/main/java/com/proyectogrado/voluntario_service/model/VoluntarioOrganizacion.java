package com.proyectogrado.voluntario_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * Vinculo voluntario <-> organizacion. Lo crea el voluntario como
 * solicitud (PENDIENTE) y la organizacion lo ACEPTA o lo RECHAZA. Un
 * voluntario puede estar vinculado a varias organizaciones, pero tiene
 * a lo sumo un registro por organizacion.
 *
 * Esta tabla es dueña de voluntario-service (la crea Hibernate con
 * ddl-auto=update); los endpoints de ambos lados viven en este servicio.
 */
@Entity
@Table(name = "voluntario_organizacion")
public class VoluntarioOrganizacion {

    public static final String PENDIENTE = "PENDIENTE";
    public static final String ACEPTADA = "ACEPTADA";
    public static final String RECHAZADA = "RECHAZADA";

    @EmbeddedId
    private VoluntarioOrganizacionId id;

    @Column(name = "estado", nullable = false)
    private String estado = PENDIENTE;

    protected VoluntarioOrganizacion() {
        // JPA
    }

    public VoluntarioOrganizacion(Integer idVoluntario, Integer idOrganizacion) {
        this.id = new VoluntarioOrganizacionId(idVoluntario, idOrganizacion);
    }

    public VoluntarioOrganizacionId getId() {
        return id;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
