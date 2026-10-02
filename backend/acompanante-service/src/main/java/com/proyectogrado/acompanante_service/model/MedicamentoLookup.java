package com.proyectogrado.acompanante_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Vista de solo lectura de la tabla medicamento, que pertenece a
 * salud-service. Permite que el acompañante vea los medicamentos de la
 * persona mayor sin repetir aquí esa lógica.
 */
@Entity
@Table(name = "medicamento")
public class MedicamentoLookup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_medicamento")
    private Integer idMedicamento;

    @Column(name = "id_persona_mayor")
    private Integer idPersonaMayor;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "dosis")
    private String dosis;

    @Column(name = "frecuencia")
    private String frecuencia;

    @Column(name = "intervalo_horas")
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

    @Column(name = "activo")
    private Boolean activo;

    /** Lo exige JPA. */

    protected MedicamentoLookup() {
    }

    public Integer getIdMedicamento() {
        return idMedicamento;
    }

    public Integer getIdPersonaMayor() {
        return idPersonaMayor;
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

    public LocalTime getHora() {
        return hora;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public LocalDateTime getProximaToma() {
        return proximaToma;
    }

    public LocalDateTime getUltimaToma() {
        return ultimaToma;
    }

    public Boolean getActivo() {
        return activo;
    }
}