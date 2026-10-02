package com.proyectogrado.actividad_service.repository;

import com.proyectogrado.actividad_service.model.PersonaMayorLookup;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Lectura de la tabla persona_mayor (ver PersonaMayorLookup).
 */
public interface PersonaMayorLookupRepository extends JpaRepository<PersonaMayorLookup, Integer> {
}
