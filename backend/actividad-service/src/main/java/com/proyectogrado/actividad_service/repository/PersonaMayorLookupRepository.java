package com.proyectogrado.actividad_service.repository;

import com.proyectogrado.actividad_service.model.PersonaMayorLookup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonaMayorLookupRepository extends JpaRepository<PersonaMayorLookup, Integer> {
}
