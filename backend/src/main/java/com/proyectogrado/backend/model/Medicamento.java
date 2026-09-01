package com.proyectogrado.backend.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "medicamento")
public class Medicamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_medicamento")
    private Integer idMedicamento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_persona_mayor", nullable = false)
    private PersonaMayor personaMayor;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "dosis")
    private String dosis;

    @Column(name = "frecuencia")
    private String frecuencia;

    @Column(name = "intervalo_horas", nullable = false)
    private Integer intervaloHoras;

    @Column(name = "hora")
    private LocalTime hora;

    @Column(name = "fecha_inicio")
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin")
    private LocalDate fechaFin;

    @Column(name = "proxima_toma")
    private LocalDateTime proximaToma;

    @Column(name = "ultima_toma")
    private LocalDateTime ultimaToma;

    @Column(name = "ultimo_recordatorio_enviado")
    private LocalDateTime ultimoRecordatorioEnviado;

    @Column(name = "activo")
    private Boolean activo;

    public Medicamento() {
    }

    @PrePersist
    protected void onCreate() {
        if (this.activo == null) {
            this.activo = true;
        }
        if (this.fechaInicio == null) {
            this.fechaInicio = LocalDate.now();
        }
    }

    public Integer getIdMedicamento() {
        return idMedicamento;
    }

    public void setIdMedicamento(Integer idMedicamento) {
        this.idMedicamento = idMedicamento;
    }

    public PersonaMayor getPersonaMayor() {
        return personaMayor;
    }

    public void setPersonaMayor(PersonaMayor personaMayor) {
        this.personaMayor = personaMayor;
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