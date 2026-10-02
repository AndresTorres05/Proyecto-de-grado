package com.proyectogrado.voluntario_service.repository;

import com.proyectogrado.voluntario_service.model.VoluntarioOrganizacion;
import com.proyectogrado.voluntario_service.model.VoluntarioOrganizacionId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VoluntarioOrganizacionRepository
        extends JpaRepository<VoluntarioOrganizacion, VoluntarioOrganizacionId> {

    List<VoluntarioOrganizacion> findById_IdVoluntario(Integer idVoluntario);

    List<VoluntarioOrganizacion> findById_IdOrganizacionAndEstado(Integer idOrganizacion, String estado);
}
