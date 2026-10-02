package com.proyectogrado.salud_backend.repository;

import com.proyectogrado.salud_backend.model.SignoVital;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Acceso a la tabla signo_vital.
 */
public interface SignoVitalRepository extends JpaRepository<SignoVital, Integer> {

    List<SignoVital> findByIdPersonaMayorOrderByFechaHoraDesc(Integer idPersonaMayor);

    List<SignoVital> findTop10ByIdPersonaMayorOrderByFechaHoraDesc(Integer idPersonaMayor);
}
