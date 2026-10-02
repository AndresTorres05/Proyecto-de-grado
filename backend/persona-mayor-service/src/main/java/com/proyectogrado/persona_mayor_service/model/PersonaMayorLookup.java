package com.proyectogrado.persona_mayor_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;

/**
 * Fila de persona_mayor. La crea auth-service durante el registro, pero la
 * fecha de nacimiento, el género y la dirección de esta tabla son datos de
 * perfil, así que este servicio sí puede modificarlos (a diferencia de
 * UsuarioLookup, que es solo de lectura).
 */
@Entity
@Table(name = "persona_mayor")
public class PersonaMayorLookup {

    @Id
    @Column(name = "id_usuario")
    private Integer idUsuario;

    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;

    @Column(name = "genero")
    private String genero;

    @Column(name = "direccion")
    private String direccion;

    /** Lo exige JPA. */
    protected PersonaMayorLookup() {
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }
}