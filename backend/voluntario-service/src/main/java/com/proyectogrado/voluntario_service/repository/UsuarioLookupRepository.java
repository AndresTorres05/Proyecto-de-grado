package com.proyectogrado.voluntario_service.repository;

import com.proyectogrado.voluntario_service.model.UsuarioLookup;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Lectura de la tabla usuario (ver UsuarioLookup).
 */
public interface UsuarioLookupRepository extends JpaRepository<UsuarioLookup, Integer> {
}
