package com.proyectogrado.backend.repository;

import com.proyectogrado.backend.model.Voluntario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoluntarioRepository extends JpaRepository<Voluntario, Integer> {
}