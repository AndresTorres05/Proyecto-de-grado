package com.proyectogrado.auth_backend.repository;

import com.proyectogrado.auth_backend.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

/**
 * Acceso a la tabla usuario. El correo y el celular sirven para encontrar
 * a la persona al iniciar sesión.
 */
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    Optional<Usuario> findByCorreo(String correo);

    boolean existsByCorreo(String correo);

    Optional<Usuario> findByCelular(String celular);

    boolean existsByCelular(String celular);

    List<Usuario> findByIdOrganizacion(Integer idOrganizacion);
}