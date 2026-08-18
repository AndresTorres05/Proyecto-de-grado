package com.proyectogrado.backend.repository;

import com.proyectogrado.backend.model.PersonaMayorGusto;
import com.proyectogrado.backend.model.PersonaMayorGustoId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PersonaMayorGustoRepository extends JpaRepository<PersonaMayorGusto, PersonaMayorGustoId> {

    @Query("select pmg from PersonaMayorGusto pmg join fetch pmg.gusto where pmg.personaMayor.idUsuario = :idPersonaMayor")
    List<PersonaMayorGusto> findByPersonaMayor_IdUsuario(@Param("idPersonaMayor") Integer idPersonaMayor);

    boolean existsByGusto_IdGusto(Integer idGusto);

    void deleteByPersonaMayor_IdUsuario(Integer idPersonaMayor);
}
