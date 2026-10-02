package com.proyectogrado.persona_mayor_service.repository;

import com.proyectogrado.persona_mayor_service.model.AcompananteLookup;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Acceso a la tabla acompanante (ver AcompananteLookup).
 */
public interface AcompananteLookupRepository extends JpaRepository<AcompananteLookup, Integer> {
}
