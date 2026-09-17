package com.proyectogrado.personamayor_service.repository;

import com.proyectogrado.personamayor_service.model.PersonaMayorLookup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonaMayorLookupRepository extends JpaRepository<PersonaMayorLookup, Integer> {
}