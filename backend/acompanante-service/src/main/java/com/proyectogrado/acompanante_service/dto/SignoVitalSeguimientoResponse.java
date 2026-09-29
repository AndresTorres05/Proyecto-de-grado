package com.proyectogrado.acompanante_service.dto;

/**
 * Misma forma que el SignoVitalResponse de salud-backend, para que el
 * frontend use una sola interfaz.
 */
public record SignoVitalSeguimientoResponse(
        Integer idSignoVital,
        String fechaHora,
        Integer presionSistolica,
        Integer presionDiastolica,
        Integer frecuenciaCardiaca,
        Double temperatura,
        Integer saturacionOxigeno,
        Integer frecuenciaRespiratoria,
        Double peso,
        String observaciones
) {
}
