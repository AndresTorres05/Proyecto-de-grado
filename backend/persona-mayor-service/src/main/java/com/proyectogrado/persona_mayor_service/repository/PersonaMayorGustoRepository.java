package com.proyectogrado.persona_mayor_service.repository;

import com.proyectogrado.persona_mayor_service.model.PersonaMayorGusto;
import com.proyectogrado.persona_mayor_service.model.PersonaMayorGustoId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Acceso a los gustos marcados por cada persona mayor.
 */
public interface PersonaMayorGustoRepository
        extends JpaRepository<PersonaMayorGusto, PersonaMayorGustoId> {

    List<PersonaMayorGusto> findById_IdPersonaMayor(Integer idPersonaMayor);

    boolean existsByGusto_IdGusto(Integer idGusto);

    /** Borra todos los gustos de la persona mayor, antes de guardar la lista nueva. */
    @Transactional
    void deleteById_IdPersonaMayor(Integer idPersonaMayor);
}
