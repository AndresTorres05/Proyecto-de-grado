package com.proyectogrado.personas_backend.repository;

import com.proyectogrado.personas_backend.model.PersonaMayorOrganizacion;
import com.proyectogrado.personas_backend.model.PersonaMayorOrganizacionId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PersonaMayorOrganizacionRepository
        extends JpaRepository<PersonaMayorOrganizacion, PersonaMayorOrganizacionId> {

    List<PersonaMayorOrganizacion> findById_IdPersonaMayorAndEstado(Integer idPersonaMayor, String estado);

    List<PersonaMayorOrganizacion> findById_IdOrganizacionAndEstado(Integer idOrganizacion, String estado);
}
