package com.proyectogrado.salud_backend.config;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Las horas de los medicamentos las escribe la persona mayor en hora de
 * Colombia. Si se usara la zona del servidor (p. ej. UTC en un despliegue)
 * los recordatorios llegarian con horas de diferencia, asi que todo calculo
 * de "ahora" pasa por aqui.
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
