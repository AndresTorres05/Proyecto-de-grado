package com.proyectogrado.auth_backend.repository;

import com.proyectogrado.auth_backend.model.UsuarioRol;
import com.proyectogrado.auth_backend.model.UsuarioRolId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UsuarioRolRepository extends JpaRepository<UsuarioRol, UsuarioRolId> {

    @Query("SELECT ur FROM UsuarioRol ur JOIN FETCH ur.rol WHERE ur.usuario.idUsuario = :idUsuario")
    List<UsuarioRol> findByUsuario_IdUsuario(@Param("idUsuario") Integer idUsuario);

    List<UsuarioRol> findByRol_Nombre(String nombre);
}