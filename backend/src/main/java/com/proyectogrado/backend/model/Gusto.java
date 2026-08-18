package com.proyectogrado.backend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "gusto")
public class Gusto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_gusto")
    private Integer idGusto;

    @Column(name = "nombre", nullable = false, unique = true)
    private String nombre;

    public Gusto() {
    }

    public Gusto(String nombre) {
        this.nombre = nombre;
    }

    public Integer getIdGusto() {
        return idGusto;
    }

    public void setIdGusto(Integer idGusto) {
        this.idGusto = idGusto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
