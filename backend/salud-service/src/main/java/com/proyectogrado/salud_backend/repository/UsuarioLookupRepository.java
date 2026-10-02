package com.proyectogrado.salud_backend.repository;

import com.proyectogrado.salud_backend.model.UsuarioLookup;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Lectura de la tabla usuario (ver UsuarioLookup).
 */
public interface UsuarioLookupRepository extends JpaRepository<UsuarioLookup, Integer> {
}
