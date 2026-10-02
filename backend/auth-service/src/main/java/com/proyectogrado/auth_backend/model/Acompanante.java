package com.proyectogrado.auth_backend.model;

import jakarta.persistence.*;

/**
 * Datos propios del acompañante (familiar o cuidador de una persona mayor).
 * Comparte la llave con usuario (id_usuario).
 */
@Entity
@Table(name = "acompanante")
public class Acompanante {

    @Id
    @Column(name = "id_usuario")
    private Integer idUsuario;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    /** Parentesco con la persona mayor (por ejemplo, "Hija"). Lo indica ella al agregarlo. */
    @Column(name = "relacion")
    private String relacion;

    public Acompanante() {
    }

    public Acompanante(Usuario usuario, String relacion) {
        this.usuario = usuario;
        this.relacion = relacion;
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getRelacion() {
        return relacion;
    }

    public void setRelacion(String relacion) {
        this.relacion = relacion;
    }
}