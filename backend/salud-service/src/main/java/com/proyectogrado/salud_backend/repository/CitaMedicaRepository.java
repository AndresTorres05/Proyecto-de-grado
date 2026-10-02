package com.proyectogrado.salud_backend.repository;

import com.proyectogrado.salud_backend.model.CitaMedica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Acceso a la tabla cita_medica, incluidas las actualizaciones con las que
 * el scheduler "reserva" cada recordatorio antes de enviarlo.
 */
public interface CitaMedicaRepository extends JpaRepository<CitaMedica, Integer> {

    List<CitaMedica> findByIdPersonaMayorOrderByFechaAscHoraAsc(Integer idPersonaMayor);

    /** Citas entre dos fechas, ambas incluidas. La usa el scheduler de recordatorios. */
    List<CitaMedica> findByFechaBetween(LocalDate desde, LocalDate hasta);

    /**
     * Reserva el aviso del día antes para el inicio indicado. Solo afecta
     * una fila si ese aviso aún no se envió para ese inicio, de modo que
     * aunque haya dos instancias del scheduler, solo una lo envía.
     */
    @Modifying
    @Transactional
    @Query("""
            UPDATE CitaMedica c
               SET c.recordatorioDiaEnviadoPara = :inicio
             WHERE c.idCita = :id
               AND (c.recordatorioDiaEnviadoPara IS NULL
                    OR c.recordatorioDiaEnviadoPara <> :inicio)
            """)
    int reservarAvisoDia(@Param("id") Integer id,
                         @Param("inicio") LocalDateTime inicio);

    /** Lo mismo que reservarAvisoDia, para el aviso de una hora antes. */
    @Modifying
    @Transactional
    @Query("""
            UPDATE CitaMedica c
               SET c.recordatorioHoraEnviadoPara = :inicio
             WHERE c.idCita = :id
               AND (c.recordatorioHoraEnviadoPara IS NULL
                    OR c.recordatorioHoraEnviadoPara <> :inicio)
            """)
    int reservarAvisoHora(@Param("id") Integer id,
                          @Param("inicio") LocalDateTime inicio);
}
