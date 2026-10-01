package com.proyectogrado.persona_mayor_service.repository;

import com.proyectogrado.persona_mayor_service.model.FelicitacionCumpleanos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface FelicitacionCumpleanosRepository
        extends JpaRepository<FelicitacionCumpleanos, FelicitacionCumpleanos.Clave> {

    /**
     * Reserva la felicitacion del año. Devuelve 1 si esta llamada la
     * reservo (hay que enviar) y 0 si ya estaba reservada (ya se envio).
     */
    @Transactional
    @Modifying
    @Query(value = """
            insert into felicitacion_cumpleanos (id_usuario, anio, fecha_envio)
            values (:idUsuario, :anio, now())
            on conflict do nothing
            """, nativeQuery = true)
    int reservar(@Param("idUsuario") Integer idUsuario, @Param("anio") Integer anio);
}
