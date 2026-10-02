package com.proyectogrado.salud_backend.repository;

import com.proyectogrado.salud_backend.model.Medicamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Acceso a la tabla medicamento, incluidas las actualizaciones con las que
 * el scheduler "reserva" cada aviso antes de enviarlo.
 */
public interface MedicamentoRepository extends JpaRepository<Medicamento, Integer> {

    List<Medicamento> findByIdPersonaMayor(Integer idPersonaMayor);

    /** Medicamentos activos cuya próxima toma cae antes del límite. */
    List<Medicamento> findByActivoTrueAndProximaTomaLessThanEqual(LocalDateTime limite);

    /**
     * Reserva el aviso previo (15 minutos antes) de la toma actual. Solo
     * afecta una fila si ese aviso aún no se ha enviado, de modo que aunque
     * haya dos instancias del scheduler, solo una lo envía.
     */
    @Modifying
    @Transactional
    @Query("""
            UPDATE Medicamento m
               SET m.ultimoRecordatorioEnviado = :ahora
             WHERE m.idMedicamento = :id
               AND m.proximaToma = :proximaToma
               AND (m.ultimoRecordatorioEnviado IS NULL
                    OR m.ultimoRecordatorioEnviado < :inicioVentana)
            """)
    int reservarAvisoPrevio(@Param("id") Integer id,
                            @Param("proximaToma") LocalDateTime proximaToma,
                            @Param("inicioVentana") LocalDateTime inicioVentana,
                            @Param("ahora") LocalDateTime ahora);

    /**
     * Reserva el aviso de la hora exacta y avanza la próxima toma. Solo
     * afecta una fila si la próxima toma sigue siendo la misma que se leyó;
     * así cada toma se avisa una sola vez.
     */
    @Modifying
    @Transactional
    @Query("""
            UPDATE Medicamento m
               SET m.ultimaToma = :proximaToma,
                   m.proximaToma = :siguienteToma,
                   m.ultimoRecordatorioEnviado = :ahora
             WHERE m.idMedicamento = :id
               AND m.proximaToma = :proximaToma
            """)
    int avanzarToma(@Param("id") Integer id,
                    @Param("proximaToma") LocalDateTime proximaToma,
                    @Param("siguienteToma") LocalDateTime siguienteToma,
                    @Param("ahora") LocalDateTime ahora);
}
