package com.proyectogrado.organizacion_service.repository;

import com.proyectogrado.organizacion_service.model.OrganizacionLookup;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Acceso a la tabla organizacion (ver OrganizacionLookup).
 */
public interface OrganizacionLookupRepository extends JpaRepository<OrganizacionLookup, Integer> {
}