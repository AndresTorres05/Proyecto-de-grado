package com.proyectogrado.salud_backend.dto;

/**
 * Cita médica tal como la devuelve la API.
 */
public class CitaMedicaResponse {

    private Integer idCita;
    private String titulo;
    private String lugar;
    private String consultorio;
    private String fecha;   // "yyyy-MM-dd"
    private String hora;    // "HH:mm"
    private String observaciones;

    public CitaMedicaResponse(
            Integer idCita,
            String titulo,
            String lugar,
            String consultorio,
            String fecha,
            String hora,
            String observaciones
    ) {
        this.idCita = idCita;
        this.titulo = titulo;
        this.lugar = lugar;
        this.consultorio = consultorio;
        this.fecha = fecha;
        this.hora = hora;
        this.observaciones = observaciones;
    }

    public Integer getIdCita() {
        return idCita;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getLugar() {
        return lugar;
    }

    public String getConsultorio() {
        return consultorio;
    }

    public String getFecha() {
        return fecha;
    }

    public String getHora() {
        return hora;
    }

    public String getObservaciones() {
        return observaciones;
    }
}