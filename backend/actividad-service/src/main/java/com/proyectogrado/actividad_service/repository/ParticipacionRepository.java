package com.proyectogrado.actividad_service.repository;

import com.proyectogrado.actividad_service.model.Participacion;
import com.proyectogrado.actividad_service.model.ParticipacionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Acceso a las inscripciones (tabla participacion).
 */
public interface ParticipacionRepository extends JpaRepository<Participacion, ParticipacionId> {

    List<Participacion> findById_IdPersonaMayor(Integer idPersonaMayor);

    List<Participacion> findById_IdActividad(Integer idActividad);

    Optional<Participacion> findById_IdPersonaMayorAndId_IdActividad(Integer idPersonaMayor, Integer idActividad);

    /**
     * Reserva el recordatorio de 1 hora antes para el inicio indicado. Solo
     * afecta una fila si todavía no se había enviado para ese inicio; así el
     * aviso sale una sola vez aunque el servicio se reinicie o haya dos
     * instancias del scheduler. Si la persona canceló la inscripción, la
     * fila ya no existe y no se avisa.
     */
    @Modifying
    @Transactional
    @Query("""
            UPDATE Participacion p
               SET p.recordatorioEnviadoPara = :inicio
             WHERE p.id.idPersonaMayor = :idPersonaMayor
               AND p.id.idActividad = :idActividad
               AND (p.recordatorioEnviadoPara IS NULL
                    OR p.recordatorioEnviadoPara <> :inicio)
            """)
    int reservarRecordatorio(@Param("idPersonaMayor") Integer idPersonaMayor,
                             @Param("idActividad") Integer idActividad,
                             @Param("inicio") LocalDateTime inicio);
}
