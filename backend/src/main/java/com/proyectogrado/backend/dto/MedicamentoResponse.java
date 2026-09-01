package com.proyectogrado.backend.dto;

public class MedicamentoResponse {

    private Integer idMedicamento;
    private String nombre;
    private String dosis;
    private String frecuencia;
    private Integer intervaloHoras;
    private String hora;
    private String fechaInicio;
    private String fechaFin;
    private String proximaToma;
    private String ultimaToma;
    private Boolean activo;

    public MedicamentoResponse() {
    }

    public MedicamentoResponse(Integer idMedicamento, String nombre, String dosis, String frecuencia,
                                Integer intervaloHoras, String hora, String fechaInicio, String fechaFin,
                                String proximaToma, String ultimaToma, Boolean activo) {
        this.idMedicamento = idMedicamento;
        this.nombre = nombre;
        this.dosis = dosis;
        this.frecuencia = frecuencia;
        this.intervaloHoras = intervaloHoras;
        this.hora = hora;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.proximaToma = proximaToma;
        this.ultimaToma = ultimaToma;
        this.activo = activo;
    }

    public Integer getIdMedicamento() {
        return idMedicamento;
    }

    public void setIdMedicamento(Integer idMedicamento) {
        this.idMedicamento = idMedicamento;
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

    public String getProximaToma() {
        return proximaToma;
    }

    public void setProximaToma(String proximaToma) {
        this.proximaToma = proximaToma;
    }

    public String getUltimaToma() {
        return ultimaToma;
    }

    public void setUltimaToma(String ultimaToma) {
        this.ultimaToma = ultimaToma;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }
}