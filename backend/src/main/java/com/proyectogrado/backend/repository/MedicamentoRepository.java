package com.proyectogrado.backend.repository;

import com.proyectogrado.backend.model.Medicamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface MedicamentoRepository extends JpaRepository<Medicamento, Integer> {

    List<Medicamento> findByPersonaMayor_IdUsuario(Integer idPersonaMayor);

    List<Medicamento> findByActivoTrueAndProximaTomaLessThanEqual(LocalDateTime ahora);
}