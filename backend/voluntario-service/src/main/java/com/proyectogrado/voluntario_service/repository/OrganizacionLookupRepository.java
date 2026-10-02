package com.proyectogrado.voluntario_service.repository;

import com.proyectogrado.voluntario_service.model.OrganizacionLookup;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Lectura de la tabla organizacion (ver OrganizacionLookup).
 */
public interface OrganizacionLookupRepository extends JpaRepository<OrganizacionLookup, Integer> {
}
