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
 * Marca que ya se aviso del cumpleaños de una persona mayor en un año.
 * La fila se "reserva" antes de enviar los mensajes (ver
 * AvisoCumpleanosRepository), asi el aviso sale una sola vez aunque el
 * scheduler corra varias veces en el dia o haya dos instancias.
 */
@Entity
@Table(name = "aviso_cumpleanos")
@IdClass(AvisoCumpleanos.Clave.class)
public class AvisoCumpleanos {

    @Id
    @Column(name = "id_persona_mayor")
    private Integer idPersonaMayor;

    @Id
    @Column(name = "anio")
    private Integer anio;

    @Column(name = "fecha_envio", nullable = false)
    private Instant fechaEnvio;

    protected AvisoCumpleanos() {
        // JPA
    }

    public Integer getIdPersonaMayor() {
        return idPersonaMayor;
    }

    public Integer getAnio() {
        return anio;
    }

    public Instant getFechaEnvio() {
        return fechaEnvio;
    }

    public static class Clave implements Serializable {

        private Integer idPersonaMayor;
        private Integer anio;

        public Clave() {
        }

        public Clave(Integer idPersonaMayor, Integer anio) {
            this.idPersonaMayor = idPersonaMayor;
            this.anio = anio;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Clave otro)) return false;
            return Objects.equals(idPersonaMayor, otro.idPersonaMayor)
                    && Objects.equals(anio, otro.anio);
        }

        @Override
        public int hashCode() {
            return Objects.hash(idPersonaMayor, anio);
        }
    }
}
