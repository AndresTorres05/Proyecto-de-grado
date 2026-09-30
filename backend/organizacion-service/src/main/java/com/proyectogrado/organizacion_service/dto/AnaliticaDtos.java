package com.proyectogrado.organizacion_service.dto;

import java.util.List;

/**
 * Respuestas de la analítica de la organización. Son datos "crudos"
 * (una fila por actividad, por medición, por persona); el frontend arma
 * los indicadores y las gráficas a partir de ellos.
 */
public final class AnaliticaDtos {

    private AnaliticaDtos() {
    }

    // ---------- Actividades ----------

    public record ActividadAnalitica(
            Integer idActividad,
            String nombre,
            String tipo,
            String fecha,          // YYYY-MM-DD
            Integer cupos,
            long inscritos,
            long asistentes,       // asistio = true
            long conRegistro       // asistio no es null (se tomó asistencia)
    ) {
    }

    // ---------- Salud ----------

    public record PersonaAnalitica(Integer idUsuario, String nombre) {
    }

    public record MedicionAnalitica(
            Integer idPersonaMayor,
            String fechaHora,      // YYYY-MM-DDTHH:mm
            Integer presionSistolica,
            Integer presionDiastolica,
            Integer frecuenciaCardiaca,
            Double temperatura,
            Integer saturacionOxigeno,
            Integer frecuenciaRespiratoria,
            Double peso
    ) {
    }

    public record SaludAnalitica(
            List<PersonaAnalitica> personas,
            List<MedicionAnalitica> mediciones
    ) {
    }

    // ---------- Población ----------

    public record PersonaPoblacion(
            Integer idUsuario,
            String nombre,
            String fechaNacimiento, // YYYY-MM-DD o null
            String genero,
            String eps
    ) {
    }

    public record InteresConteo(String nombre, String categoria, long personas) {
    }

    public record PoblacionAnalitica(
            List<PersonaPoblacion> personas,
            List<InteresConteo> intereses,
            long personasConIntereses
    ) {
    }
}
