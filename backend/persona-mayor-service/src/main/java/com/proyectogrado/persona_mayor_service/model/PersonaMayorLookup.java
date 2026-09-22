package com.proyectogrado.persona_mayor_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;

/**
 * "PersonaMayor" nace en auth-backend junto con el Usuario (misma
 * transaccion del registro). Pero fechaNacimiento/genero/direccion son
 * datos de perfil propio, no de identidad -> este servicio SI puede
 * escribirlos, a diferencia de UsuarioLookup (100% solo lectura).
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

    protected PersonaMayorLookup() {
        // JPA
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