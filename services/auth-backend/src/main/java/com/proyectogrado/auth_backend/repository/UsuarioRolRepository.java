package com.proyectogrado.auth_backend.repository;

import com.proyectogrado.auth_backend.model.UsuarioRol;
import com.proyectogrado.auth_backend.model.UsuarioRolId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UsuarioRolRepository extends JpaRepository<UsuarioRol, UsuarioRolId> {

    List<UsuarioRol> findByUsuario_IdUsuario(Integer idUsuario);

    List<UsuarioRol> findByRol_Nombre(String nombre);
}