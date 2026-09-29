package com.proyectogrado.salud_backend.repository;

import com.proyectogrado.salud_backend.model.Medicamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

public interface MedicamentoRepository extends JpaRepository<Medicamento, Integer> {

    List<Medicamento> findByIdPersonaMayor(Integer idPersonaMayor);

    List<Medicamento> findByActivoTrueAndProximaTomaLessThanEqual(LocalDateTime limite);

    /**
     * Reserva el aviso previo (15 min antes) de la toma actual. Solo
     * afecta una fila si ese aviso aun no se ha enviado, de modo que
     * aunque haya dos instancias del scheduler solo una lo envia.
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
     * Reserva el aviso a la hora exacta y avanza la proxima toma. Solo
     * afecta una fila si la proxima toma sigue siendo la misma que se leyo,
     * asi cada toma se avisa una sola vez.
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
