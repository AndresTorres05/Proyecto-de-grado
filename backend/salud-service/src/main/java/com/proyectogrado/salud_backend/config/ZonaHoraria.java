package com.proyectogrado.salud_backend.config;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Fecha y hora actuales en Colombia. La persona mayor escribe las horas de
 * sus medicamentos en hora colombiana; si se usara la zona del servidor
 * (por ejemplo UTC en un despliegue), los recordatorios llegarían con horas
 * de diferencia. Por eso todo cálculo de "ahora" pasa por aquí.
 */
public final class ZonaHoraria {

    public static final ZoneId COLOMBIA = ZoneId.of("America/Bogota");

    private ZonaHoraria() {
    }

    public static LocalDateTime ahora() {
        return LocalDateTime.now(COLOMBIA);
    }

    public static LocalDate hoy() {
        return LocalDate.now(COLOMBIA);
    }
}
