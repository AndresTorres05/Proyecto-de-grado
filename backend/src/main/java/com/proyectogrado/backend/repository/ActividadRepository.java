package com.proyectogrado.backend.repository;

import com.proyectogrado.backend.model.Actividad;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActividadRepository extends JpaRepository<Actividad, Integer> {

    List<Actividad> findByIdOrganizacion(Integer idOrganizacion);
}
