package com.proyectogrado.actividad_service.repository;

import com.proyectogrado.actividad_service.model.PersonaMayorOrganizacionId;
import com.proyectogrado.actividad_service.model.PersonaMayorOrganizacionLookup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PersonaMayorOrganizacionLookupRepository
        extends JpaRepository<PersonaMayorOrganizacionLookup, PersonaMayorOrganizacionId> {

    List<PersonaMayorOrganizacionLookup> findById_IdPersonaMayorAndEstado(Integer idPersonaMayor, String estado);
}
