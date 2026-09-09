package com.proyectogrado.backend.repository;

import com.proyectogrado.backend.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    Optional<Usuario> findByCorreo(String correo);

    boolean existsByCorreo(String correo);

    Optional<Usuario> findByTelefono(String telefono);

    boolean existsByTelefono(String telefono);

    Optional<Usuario> findByIdOrganizacion(Integer idOrganizacion);
}