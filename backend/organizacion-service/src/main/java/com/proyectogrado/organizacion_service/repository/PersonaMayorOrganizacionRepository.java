package com.proyectogrado.organizacion_service.repository;

import com.proyectogrado.organizacion_service.model.PersonaMayorOrganizacion;
import com.proyectogrado.organizacion_service.model.PersonaMayorOrganizacionId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PersonaMayorOrganizacionRepository
        extends JpaRepository<PersonaMayorOrganizacion, PersonaMayorOrganizacionId> {

    List<PersonaMayorOrganizacion> findById_IdOrganizacionAndEstado(Integer idOrganizacion, String estado);
}
