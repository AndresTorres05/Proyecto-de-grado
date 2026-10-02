package com.proyectogrado.acompanante_service.repository;

import com.proyectogrado.acompanante_service.model.SignoVitalLookup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Lectura de la tabla signo_vital (ver SignoVitalLookup).
 */
public interface SignoVitalLookupRepository extends JpaRepository<SignoVitalLookup, Integer> {

    List<SignoVitalLookup> findTop10ByIdPersonaMayorOrderByFechaHoraDesc(Integer idPersonaMayor);
}
