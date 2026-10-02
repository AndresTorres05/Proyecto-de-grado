package com.proyectogrado.actividad_service.repository;

import com.proyectogrado.actividad_service.model.OrganizacionLookup;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Lectura de la tabla organizacion (ver OrganizacionLookup).
 */
public interface OrganizacionLookupRepository extends JpaRepository<OrganizacionLookup, Integer> {
}
