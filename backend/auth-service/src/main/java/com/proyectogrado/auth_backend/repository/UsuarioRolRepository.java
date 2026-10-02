package com.proyectogrado.auth_backend.repository;

import com.proyectogrado.auth_backend.model.UsuarioRol;
import com.proyectogrado.auth_backend.model.UsuarioRolId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Acceso a los roles asignados a cada usuario (tabla usuario_rol).
 */
public interface UsuarioRolRepository extends JpaRepository<UsuarioRol, UsuarioRolId> {

    /**
     * Roles del usuario con el rol ya cargado (JOIN FETCH), para poder leer
     * su nombre aunque no haya una transacción abierta.
     */
    @Query("SELECT ur FROM UsuarioRol ur JOIN FETCH ur.rol WHERE ur.usuario.idUsuario = :idUsuario")
    List<UsuarioRol> findByUsuario_IdUsuario(@Param("idUsuario") Integer idUsuario);

    List<UsuarioRol> findByRol_Nombre(String nombre);
}