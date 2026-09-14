package com.proyectogrado.personas_backend.repository;

import com.proyectogrado.personas_backend.model.UsuarioLookup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioLookupRepository extends JpaRepository<UsuarioLookup, Integer> {

    Optional<UsuarioLookup> findByTelefono(String telefono);

    List<UsuarioLookup> findByIdOrganizacion(Integer idOrganizacion);
}
