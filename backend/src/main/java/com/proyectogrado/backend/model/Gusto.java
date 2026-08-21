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

    @Column(name = "categoria", length = 20)
    private String categoria;

    public Gusto() {
    }

    public Gusto(String nombre) {
        this.nombre = nombre;
    }

    public Gusto(String nombre, String categoria) {
        this.nombre = nombre;
        this.categoria = categoria;
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

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }
}