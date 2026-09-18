package com.proyectogrado.auth_backend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "voluntario")
public class Voluntario {

    @Id
    @Column(name = "id_usuario")
    private Integer idUsuario;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @Column(name = "disponibilidad")
    private String disponibilidad;

    public Voluntario() {
    }

    public Voluntario(Usuario usuario, String disponibilidad) {
        this.usuario = usuario;
        this.disponibilidad = disponibilidad;
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

    public String getDisponibilidad() {
        return disponibilidad;
    }

    public void setDisponibilidad(String disponibilidad) {
        this.disponibilidad = disponibilidad;
    }
}