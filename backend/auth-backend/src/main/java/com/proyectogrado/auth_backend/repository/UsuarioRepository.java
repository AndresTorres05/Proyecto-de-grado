package com.proyectogrado.auth_backend.repository;

import com.proyectogrado.auth_backend.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;


public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    Optional<Usuario> findByCorreo(String correo);

    boolean existsByCorreo(String correo);

    Optional<Usuario> findByTelefono(String telefono);

    boolean existsByTelefono(String telefono);

    List<Usuario> findByIdOrganizacion(Integer idOrganizacion);
}