package com.proyectogrado.persona_mayor_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * Marca que VITA+ ya felicito a un usuario (de cualquier rol) en un año.
 * Funciona igual que AvisoCumpleanos: la fila se reserva antes de enviar
 * el mensaje, asi la felicitacion sale una sola vez.
 */
@Entity
@Table(name = "felicitacion_cumpleanos")
@IdClass(FelicitacionCumpleanos.Clave.class)
public class FelicitacionCumpleanos {

    @Id
    @Column(name = "id_usuario")
    private Integer idUsuario;

    @Id
    @Column(name = "anio")
    private Integer anio;

    @Column(name = "fecha_envio", nullable = false)
    private Instant fechaEnvio;

    protected FelicitacionCumpleanos() {
        // JPA
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public Integer getAnio() {
        return anio;
    }

    public Instant getFechaEnvio() {
        return fechaEnvio;
    }

    public static class Clave implements Serializable {

        private Integer idUsuario;
        private Integer anio;

        public Clave() {
        }

        public Clave(Integer idUsuario, Integer anio) {
            this.idUsuario = idUsuario;
            this.anio = anio;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Clave otro)) return false;
            return Objects.equals(idUsuario, otro.idUsuario)
                    && Objects.equals(anio, otro.anio);
        }

        @Override
        public int hashCode() {
            return Objects.hash(idUsuario, anio);
        }
    }
}
