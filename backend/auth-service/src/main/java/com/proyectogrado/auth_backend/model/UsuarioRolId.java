package com.proyectogrado.auth_backend.model;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

/**
 * Llave compuesta de usuario_rol: id del usuario más id del rol.
 */
@Embeddable
public class UsuarioRolId implements Serializable {

    private Integer idUsuario;

    private Integer idRol;

    public UsuarioRolId() {
    }

    public UsuarioRolId(Integer idUsuario, Integer idRol) {
        this.idUsuario = idUsuario;
        this.idRol = idRol;
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Integer getIdRol() {
        return idRol;
    }

    public void setIdRol(Integer idRol) {
        this.idRol = idRol;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof UsuarioRolId)) {
            return false;
        }

        UsuarioRolId that = (UsuarioRolId) o;

        return Objects.equals(idUsuario, that.idUsuario)
                && Objects.equals(idRol, that.idRol);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idUsuario, idRol);
    }
}