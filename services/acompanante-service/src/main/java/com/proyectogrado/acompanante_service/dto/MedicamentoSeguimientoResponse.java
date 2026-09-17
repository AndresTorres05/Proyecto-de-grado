package com.proyectogrado.acompanante_service.dto;

public class MedicamentoSeguimientoResponse {

    private Integer idMedicamento;
    private String nombre;
    private String dosis;
    private String frecuencia;
    private Integer intervaloHoras;
    private String hora;
    private String proximaToma;
    private String ultimaToma;
    private Boolean activo;

    public MedicamentoSeguimientoResponse(Integer idMedicamento, String nombre, String dosis, String frecuencia,
                                           Integer intervaloHoras, String hora, String proximaToma,
                                           String ultimaToma, Boolean activo) {
        this.idMedicamento = idMedicamento;
        this.nombre = nombre;
        this.dosis = dosis;
        this.frecuencia = frecuencia;
        this.intervaloHoras = intervaloHoras;
        this.hora = hora;
        this.proximaToma = proximaToma;
        this.ultimaToma = ultimaToma;
        this.activo = activo;
    }

    public Integer getIdMedicamento() {
        return idMedicamento;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDosis() {
        return dosis;
    }

    public String getFrecuencia() {
        return frecuencia;
    }

    public Integer getIntervaloHoras() {
        return intervaloHoras;
    }

    public String getHora() {
        return hora;
    }

    public String getProximaToma() {
        return proximaToma;
    }

    public String getUltimaToma() {
        return ultimaToma;
    }

    public Boolean getActivo() {
        return activo;
    }
}