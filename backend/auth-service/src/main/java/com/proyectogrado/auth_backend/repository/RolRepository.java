package com.proyectogrado.auth_backend.repository;

import com.proyectogrado.auth_backend.model.Rol;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Acceso al catálogo de roles.
 */
public interface RolRepository extends JpaRepository<Rol, Integer> {

    Optional<Rol> findByNombre(String nombre);
}