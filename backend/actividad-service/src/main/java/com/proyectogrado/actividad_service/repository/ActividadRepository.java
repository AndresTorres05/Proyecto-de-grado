package com.proyectogrado.actividad_service.repository;

import com.proyectogrado.actividad_service.model.Actividad;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ActividadRepository extends JpaRepository<Actividad, Integer> {

    List<Actividad> findByIdOrganizacion(Integer idOrganizacion);

    List<Actividad> findByFechaBetween(LocalDate desde, LocalDate hasta);
}
