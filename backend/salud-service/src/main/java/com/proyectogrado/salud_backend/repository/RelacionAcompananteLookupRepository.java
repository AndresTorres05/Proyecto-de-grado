package com.proyectogrado.salud_backend.repository;

import com.proyectogrado.salud_backend.model.RelacionAcompananteLookup;
import com.proyectogrado.salud_backend.model.RelacionAcompananteId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RelacionAcompananteLookupRepository
        extends JpaRepository<RelacionAcompananteLookup, RelacionAcompananteId> {

    List<RelacionAcompananteLookup> findById_IdPersonaMayorAndEstado(Integer idPersonaMayor, String estado);
}
