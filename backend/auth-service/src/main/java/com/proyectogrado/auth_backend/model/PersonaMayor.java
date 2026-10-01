package com.proyectogrado.auth_backend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "persona_mayor")
public class PersonaMayor {

    @Id
    @Column(name = "id_usuario")
    private Integer idUsuario;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    // Entidad promotora de salud (afiliación)
    @Column(name = "eps")
    private String eps;

    // Institución prestadora de salud (donde lo atienden)
    @Column(name = "ips")
    private String ips;

    public PersonaMayor() {
    }

    public PersonaMayor(Usuario usuario) {
        this.usuario = usuario;
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

    public String getEps() {
        return eps;
    }

    public void setEps(String eps) {
        this.eps = eps;
    }

    public String getIps() {
        return ips;
    }

    public void setIps(String ips) {
        this.ips = ips;
    }
}