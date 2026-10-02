package com.proyectogrado.acompanante_service.dto;

/**
 * Cita médica tal como la ve el acompañante. La fecha va en "yyyy-MM-dd" y
 * la hora en "HH:mm".
 */
public class CitaMedicaSeguimientoResponse {

    private Integer idCita;
    private String titulo;
    private String lugar;
    private String consultorio;
    private String fecha;
    private String hora;
    private String observaciones;

    public CitaMedicaSeguimientoResponse(Integer idCita, String titulo, String lugar, String consultorio,
                                         String fecha, String hora, String observaciones) {
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
