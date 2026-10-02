package com.proyectogrado.acompanante_service.repository;

import com.proyectogrado.acompanante_service.model.CitaMedicaLookup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Lectura de la tabla cita_medica (ver CitaMedicaLookup).
 */
public interface CitaMedicaLookupRepository extends JpaRepository<CitaMedicaLookup, Integer> {

    List<CitaMedicaLookup> findByIdPersonaMayorOrderByFechaAscHoraAsc(Integer idPersonaMayor);
}
