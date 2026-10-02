package com.proyectogrado.actividad_service.repository;

import com.proyectogrado.actividad_service.model.AcompananteLookup;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Lectura de la tabla acompanante (ver AcompananteLookup).
 */
public interface AcompananteLookupRepository extends JpaRepository<AcompananteLookup, Integer> {
}
