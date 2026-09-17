package com.proyectogrado.organizacion_service.repository;

import com.proyectogrado.organizacion_service.model.UsuarioLookup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioLookupRepository extends JpaRepository<UsuarioLookup, Integer> {

    Optional<UsuarioLookup> findByTelefono(String telefono);
}
