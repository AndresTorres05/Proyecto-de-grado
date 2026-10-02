package com.proyectogrado.acompanante_service.repository;

import com.proyectogrado.acompanante_service.model.AcompananteInfoLookup;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Lectura de la tabla acompanante (ver AcompananteInfoLookup).
 */
public interface AcompananteInfoLookupRepository extends JpaRepository<AcompananteInfoLookup, Integer> {
}