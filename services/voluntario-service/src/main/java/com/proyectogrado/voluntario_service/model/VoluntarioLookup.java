package com.proyectogrado.voluntario_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * "Voluntario" es dueño de auth-backend (nace con el registro).
 * voluntario-service solo necesita y solo puede tocar la columna
 * "disponibilidad", que es propia de este servicio a nivel funcional
 * (a diferencia de UsuarioLookup, que es 100% solo lectura).
 */
@Entity
@Table(name = "voluntario")
public class VoluntarioLookup {

    @Id
    @Column(name = "id_usuario")
    private Integer idUsuario;

    @Column(name = "disponibilidad")
    private String disponibilidad;

    protected VoluntarioLookup() {
        // JPA
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public String getDisponibilidad() {
        return disponibilidad;
    }

    public void setDisponibilidad(String disponibilidad) {
        this.disponibilidad = disponibilidad;
    }
}
