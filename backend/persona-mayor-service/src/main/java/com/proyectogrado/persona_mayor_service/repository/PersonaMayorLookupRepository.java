package com.proyectogrado.persona_mayor_service.repository;

import com.proyectogrado.persona_mayor_service.model.PersonaMayorLookup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonaMayorLookupRepository extends JpaRepository<PersonaMayorLookup, Integer> {
}