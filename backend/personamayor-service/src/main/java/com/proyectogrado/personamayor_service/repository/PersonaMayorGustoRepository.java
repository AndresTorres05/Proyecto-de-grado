package com.proyectogrado.personamayor_service.repository;

import com.proyectogrado.personamayor_service.model.PersonaMayorGusto;
import com.proyectogrado.personamayor_service.model.PersonaMayorGustoId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface PersonaMayorGustoRepository
        extends JpaRepository<PersonaMayorGusto, PersonaMayorGustoId> {

    List<PersonaMayorGusto> findById_IdPersonaMayor(Integer idPersonaMayor);

    boolean existsByGusto_IdGusto(Integer idGusto);

    @Transactional
    void deleteById_IdPersonaMayor(Integer idPersonaMayor);
}
