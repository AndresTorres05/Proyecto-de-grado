package com.proyectogrado.salud_backend.repository;

import com.proyectogrado.salud_backend.model.Medicamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface MedicamentoRepository extends JpaRepository<Medicamento, Integer> {

    List<Medicamento> findByIdPersonaMayor(Integer idPersonaMayor);

    List<Medicamento> findByActivoTrueAndProximaTomaLessThanEqual(LocalDateTime ahora);
}
