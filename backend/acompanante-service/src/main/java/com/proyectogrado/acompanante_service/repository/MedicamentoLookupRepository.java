package com.proyectogrado.acompanante_service.repository;

import com.proyectogrado.acompanante_service.model.MedicamentoLookup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Lectura de la tabla medicamento (ver MedicamentoLookup).
 */
public interface MedicamentoLookupRepository extends JpaRepository<MedicamentoLookup, Integer> {

    List<MedicamentoLookup> findByIdPersonaMayor(Integer idPersonaMayor);
}