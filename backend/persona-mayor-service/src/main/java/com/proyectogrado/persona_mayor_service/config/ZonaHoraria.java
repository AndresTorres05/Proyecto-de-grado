package com.proyectogrado.persona_mayor_service.config;

import java.time.LocalDate;
import java.time.ZoneId;

/**
 * Los cumpleaños se cuentan en hora de Colombia. Si se usara la zona del
 * servidor (p. ej. UTC en un despliegue) el aviso podria salir el dia
 * anterior en la noche.
 */
public final class ZonaHoraria {

    public static final ZoneId COLOMBIA = ZoneId.of("America/Bogota");

    private ZonaHoraria() {
    }

    public static LocalDate hoy() {
        return LocalDate.now(COLOMBIA);
    }
}
