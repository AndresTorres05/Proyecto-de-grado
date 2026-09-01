package com.proyectogrado.backend.dto;

public class MedicamentoRequest {

    private String nombre;
    private String dosis;
    private String frecuencia;
    private Integer intervaloHoras;
    private String hora;         // formato "HH:mm", hora de la primera toma
    private String fechaInicio;  // formato "yyyy-MM-dd", opcional
    private String fechaFin;     // formato "yyyy-MM-dd", opcional

    public MedicamentoRequest() {
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDosis() {
        return dosis;
    }

    public void setDosis(String dosis) {
        this.dosis = dosis;
    }

    public String getFrecuencia() {
        return frecuencia;
    }

    public void setFrecuencia(String frecuencia) {
        this.frecuencia = frecuencia;
    }

    public Integer getIntervaloHoras() {
        return intervaloHoras;
    }

    public void setIntervaloHoras(Integer intervaloHoras) {
        this.intervaloHoras = intervaloHoras;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(String fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public String getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(String fechaFin) {
        this.fechaFin = fechaFin;
    }
}