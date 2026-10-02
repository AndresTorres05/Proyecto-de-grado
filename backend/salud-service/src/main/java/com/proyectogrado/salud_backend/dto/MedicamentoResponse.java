package com.proyectogrado.salud_backend.dto;

/**
 * Medicamento tal como lo recibe el frontend.
 */
public class MedicamentoResponse {

    private Integer idMedicamento;
    private String nombre;
    private String dosis;
    private String frecuencia;    // "Descripción" en pantalla
    private Integer intervaloHoras;
    private String hora;          // "HH:mm"
    private String fechaInicio;   // "yyyy-MM-dd"
    private String fechaFin;      // "yyyy-MM-dd"
    private String proximaToma;   // "yyyy-MM-ddTHH:mm"
    private String ultimaToma;    // "yyyy-MM-ddTHH:mm"
    private Boolean activo;

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

    public String getFechaInicio() {
        return fechaInicio;
    }

    public String getFechaFin() {
        return fechaFin;
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
