package com.proyectogrado.auth_backend.model;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

/**
 * Rol asignado a un usuario. La llave es la pareja (usuario, rol).
 */
@Entity
@Table(name = "usuario_rol")
public class UsuarioRol {

    @EmbeddedId
    private UsuarioRolId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("idUsuario")
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("idRol")
    @JoinColumn(name = "id_rol")
    private Rol rol;

    public UsuarioRol() {
    }

    public UsuarioRol(Usuario usuario, Rol rol) {
        this.usuario = usuario;
        this.rol = rol;

        if (usuario != null && rol != null) {
            this.id = new UsuarioRolId(
                    usuario.getIdUsuario(),
                    rol.getIdRol()
            );
        }
    }

    public UsuarioRolId getId() {
        return id;
    }

    public void setId(UsuarioRolId id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;

        actualizarId();
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;

        actualizarId();
    }

    /** Mantiene la llave compuesta al día cuando cambia el usuario o el rol. */
    private void actualizarId() {

        if (usuario != null
                && usuario.getIdUsuario() != null
                && rol != null
                && rol.getIdRol() != null) {

            this.id = new UsuarioRolId(
                    usuario.getIdUsuario(),
                    rol.getIdRol()
            );
        }
    }
}