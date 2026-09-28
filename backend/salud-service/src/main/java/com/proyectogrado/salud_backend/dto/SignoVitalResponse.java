package com.proyectogrado.salud_backend.dto;

public class SignoVitalResponse {

    private Integer idSignoVital;
    private String fechaHora;
    private Integer presionSistolica;
    private Integer presionDiastolica;
    private Integer frecuenciaCardiaca;
    private Double temperatura;
    private Integer saturacionOxigeno;
    private Integer frecuenciaRespiratoria;
    private Double peso;
    private String observaciones;

    public SignoVitalResponse(
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
        this.idSignoVital = idSignoVital;
        this.fechaHora = fechaHora;
        this.presionSistolica = presionSistolica;
        this.presionDiastolica = presionDiastolica;
        this.frecuenciaCardiaca = frecuenciaCardiaca;
        this.temperatura = temperatura;
        this.saturacionOxigeno = saturacionOxigeno;
        this.frecuenciaRespiratoria = frecuenciaRespiratoria;
        this.peso = peso;
        this.observaciones = observaciones;
    }

    public Integer getIdSignoVital() {
        return idSignoVital;
    }

    public String getFechaHora() {
        return fechaHora;
    }

    public Integer getPresionSistolica() {
        return presionSistolica;
    }

    public Integer getPresionDiastolica() {
        return presionDiastolica;
    }

    public Integer getFrecuenciaCardiaca() {
        return frecuenciaCardiaca;
    }

    public Double getTemperatura() {
        return temperatura;
    }

    public Integer getSaturacionOxigeno() {
        return saturacionOxigeno;
    }

    public Integer getFrecuenciaRespiratoria() {
        return frecuenciaRespiratoria;
    }

    public Double getPeso() {
        return peso;
    }

    public String getObservaciones() {
        return observaciones;
    }
}