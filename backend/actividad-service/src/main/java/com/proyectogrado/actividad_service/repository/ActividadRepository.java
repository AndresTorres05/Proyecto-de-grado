package com.proyectogrado.actividad_service.repository;

import com.proyectogrado.actividad_service.model.Actividad;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActividadRepository extends JpaRepository<Actividad, Integer> {

    List<Actividad> findByIdOrganizacion(Integer idOrganizacion);
}
