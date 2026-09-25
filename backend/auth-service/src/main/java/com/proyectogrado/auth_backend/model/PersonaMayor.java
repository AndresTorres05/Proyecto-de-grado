package com.proyectogrado.auth_backend.model;

import jakarta.persistence.*;
import java.time.LocalDate;

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
}