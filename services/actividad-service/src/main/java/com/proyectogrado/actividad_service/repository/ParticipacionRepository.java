package com.proyectogrado.actividad_service.repository;

import com.proyectogrado.actividad_service.model.Participacion;
import com.proyectogrado.actividad_service.model.ParticipacionId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ParticipacionRepository extends JpaRepository<Participacion, ParticipacionId> {

    List<Participacion> findById_IdPersonaMayor(Integer idPersonaMayor);

    List<Participacion> findById_IdActividad(Integer idActividad);

    Optional<Participacion> findById_IdPersonaMayorAndId_IdActividad(Integer idPersonaMayor, Integer idActividad);
}
