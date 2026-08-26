package com.proyectogrado.backend.model;

import jakarta.persistence.*;

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

    @Column(name = "parentesco")
    private String parentesco;

    public Acompanante() {
    }

    public Acompanante(Usuario usuario, String parentesco) {
        this.usuario = usuario;
        this.idUsuario = usuario.getIdUsuario();
        this.parentesco = parentesco;
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

        if (usuario != null) {
            this.idUsuario = usuario.getIdUsuario();
        }
    }

    public String getParentesco() {
        return parentesco;
    }

    public void setParentesco(String parentesco) {
        this.parentesco = parentesco;
    }
}