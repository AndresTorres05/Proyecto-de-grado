package com.proyectogrado.persona_mayor_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * "Acompanante" tambien es dueno de auth-backend (nace con el registro).
 * personamayor-service solo necesita y solo puede tocar la columna
 * "parentesco", que describe la RELACION (ej: "hijo", "vecina"), no la
 * identidad del acompanante -> por eso se permite escribirla desde aqui,
 * a diferencia de UsuarioLookup que es 100% solo lectura.
 */
@Entity
@Table(name = "acompanante")
public class AcompananteLookup {

    @Id
    @Column(name = "id_usuario")
    private Integer idUsuario;

    @Column(name = "parentesco")
    private String parentesco;

    protected AcompananteLookup() {
        // JPA
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public String getParentesco() {
        return parentesco;
    }

    public void setParentesco(String parentesco) {
        this.parentesco = parentesco;
    }
}
