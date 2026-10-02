package com.proyectogrado.actividad_service.config;

import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Fecha y hora actuales en Colombia. Las organizaciones escriben la fecha y
 * la hora de las actividades en hora colombiana; si se usara la zona del
 * servidor (por ejemplo UTC en un despliegue), los recordatorios llegarían
 * con horas de diferencia. Por eso el "ahora" de los recordatorios pasa por aquí.
 */
public final class ZonaHoraria {

    public static final ZoneId COLOMBIA = ZoneId.of("America/Bogota");

    private ZonaHoraria() {
    }

    public static LocalDateTime ahora() {
        return LocalDateTime.now(COLOMBIA);
    }
}
