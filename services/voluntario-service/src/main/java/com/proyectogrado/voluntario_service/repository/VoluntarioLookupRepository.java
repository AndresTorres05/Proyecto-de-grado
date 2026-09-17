package com.proyectogrado.voluntario_service.repository;

import com.proyectogrado.voluntario_service.model.VoluntarioLookup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoluntarioLookupRepository extends JpaRepository<VoluntarioLookup, Integer> {
}
