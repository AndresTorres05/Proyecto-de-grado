package com.proyectogrado.actividad_service.config;

import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Las organizaciones escriben la fecha y hora de las actividades en hora de
 * Colombia. Si se usara la zona del servidor (p. ej. UTC en un despliegue)
 * los recordatorios llegarian con horas de diferencia, asi que el "ahora"
 * de los recordatorios pasa por aqui.
 */
public final class ZonaHoraria {

    public static final ZoneId COLOMBIA = ZoneId.of("America/Bogota");

    private ZonaHoraria() {
    }

    public static LocalDateTime ahora() {
        return LocalDateTime.now(COLOMBIA);
    }
}
