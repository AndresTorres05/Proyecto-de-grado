package com.proyectogrado.backend.repository;

import com.proyectogrado.backend.model.UsuarioRol;
import com.proyectogrado.backend.model.UsuarioRolId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UsuarioRolRepository extends JpaRepository<UsuarioRol, UsuarioRolId> {

    @Query("select ur from UsuarioRol ur join fetch ur.rol where ur.usuario.idUsuario = :idUsuario")
    List<UsuarioRol> findByUsuario_IdUsuario(@Param("idUsuario") Integer idUsuario);

    boolean existsByRol_IdRol(Integer idRol);
}
