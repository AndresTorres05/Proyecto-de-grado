package com.proyectogrado.salud_backend.repository;

import com.proyectogrado.salud_backend.model.CitaMedica;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CitaMedicaRepository extends JpaRepository<CitaMedica, Integer> {

    List<CitaMedica> findByIdPersonaMayorOrderByFechaAscHoraAsc(Integer idPersonaMayor);
}