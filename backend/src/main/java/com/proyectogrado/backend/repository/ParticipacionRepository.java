package com.proyectogrado.backend.repository;

import com.proyectogrado.backend.model.Participacion;
import com.proyectogrado.backend.model.ParticipacionId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ParticipacionRepository extends JpaRepository<Participacion, ParticipacionId> {

    List<Participacion> findById_IdPersonaMayor(Integer idPersonaMayor);

    Optional<Participacion> findById_IdPersonaMayorAndId_IdActividad(Integer idPersonaMayor, Integer idActividad);

    List<Participacion> findById_IdActividad(Integer idActividad);
}