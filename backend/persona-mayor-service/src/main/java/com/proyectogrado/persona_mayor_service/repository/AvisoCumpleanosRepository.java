package com.proyectogrado.persona_mayor_service.repository;

import com.proyectogrado.persona_mayor_service.model.AvisoCumpleanos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface AvisoCumpleanosRepository
        extends JpaRepository<AvisoCumpleanos, AvisoCumpleanos.Clave> {

    /**
     * Reserva el aviso del año. Devuelve 1 si esta llamada lo reservo
     * (hay que enviar) y 0 si ya estaba reservado (ya se envio).
     */
    @Transactional
    @Modifying
    @Query(value = """
            insert into aviso_cumpleanos (id_persona_mayor, anio, fecha_envio)
            values (:idPersonaMayor, :anio, now())
            on conflict do nothing
            """, nativeQuery = true)
    int reservar(@Param("idPersonaMayor") Integer idPersonaMayor, @Param("anio") Integer anio);
}
