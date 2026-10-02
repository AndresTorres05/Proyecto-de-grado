package com.proyectogrado.salud_backend.model;

import com.proyectogrado.salud_backend.config.ZonaHoraria;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Medicamento de una persona mayor, con su horario y el estado de sus
 * recordatorios. Esta tabla es de salud-service.
 *
 * idPersonaMayor es un Integer simple y no una relación JPA, porque la
 * tabla persona_mayor pertenece a auth-service.
 */
@Entity
@Table(name = "medicamento")
public class Medicamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_medicamento")
    private Integer idMedicamento;

    @Column(name = "id_persona_mayor", nullable = false)
    private Integer idPersonaMayor;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "dosis")
    private String dosis;

    /** En pantalla se llama "Descripción": texto libre, por ejemplo "Después de cada comida". */
    @Column(name = "frecuencia")
    private String frecuencia;

    /** Cada cuántas horas se toma; es lo que usa el scheduler para avanzar las tomas. */
    @Column(name = "intervalo_horas", nullable = false)
    private Integer intervaloHoras;

    /** Hora de referencia de las tomas, en hora de Colombia. */
    @Column(name = "hora")
    private LocalTime hora;

    @Column(name = "fecha_inicio")
    private LocalDate fechaInicio;

    /** Opcional: después de esta fecha ya no se envían recordatorios. */
    @Column(name = "fecha_fin")
    private LocalDate fechaFin;

    @Column(name = "proxima_toma")
    private LocalDateTime proximaToma;

    @Column(name = "ultima_toma")
    private LocalDateTime ultimaToma;

    /** Momento del último aviso enviado; evita repetir avisos (ver MedicamentoRepository). */
    @Column(name = "ultimo_recordatorio_enviado")
    private LocalDateTime ultimoRecordatorioEnviado;

    @Column(name = "activo")
    private Boolean activo;

    public Medicamento() {
    }

    /** Por defecto, el medicamento queda activo y empieza hoy. */
    @PrePersist
    protected void onCreate() {
        if (this.activo == null) {
            this.activo = true;
        }
        if (this.fechaInicio == null) {
            this.fechaInicio = ZonaHoraria.hoy();
        }
    }

    public Integer getIdMedicamento() {
        return idMedicamento;
    }

    public void setIdMedicamento(Integer idMedicamento) {
        this.idMedicamento = idMedicamento;
    }

    public Integer getIdPersonaMayor() {
        return idPersonaMayor;
    }

    public void setIdPersonaMayor(Integer idPersonaMayor) {
        this.idPersonaMayor = idPersonaMayor;
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

    public LocalTime getHora() {
        return hora;
    }

    public void setHora(LocalTime hora) {
        this.hora = hora;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public LocalDateTime getProximaToma() {
        return proximaToma;
    }

    public void setProximaToma(LocalDateTime proximaToma) {
        this.proximaToma = proximaToma;
    }

    public LocalDateTime getUltimaToma() {
        return ultimaToma;
    }

    public void setUltimaToma(LocalDateTime ultimaToma) {
        this.ultimaToma = ultimaToma;
    }

    public LocalDateTime getUltimoRecordatorioEnviado() {
        return ultimoRecordatorioEnviado;
    }

    public void setUltimoRecordatorioEnviado(LocalDateTime ultimoRecordatorioEnviado) {
        this.ultimoRecordatorioEnviado = ultimoRecordatorioEnviado;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }
}
