package com.proyectogrado.personamayor_service.repository;

import com.proyectogrado.personamayor_service.model.UsuarioLookup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioLookupRepository extends JpaRepository<UsuarioLookup, Integer> {

    Optional<UsuarioLookup> findByTelefono(String telefono);

    List<UsuarioLookup> findByIdOrganizacion(Integer idOrganizacion);
}
