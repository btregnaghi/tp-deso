package ar.edu.utn.frsf4.api.repository;

import ar.edu.utn.frsf4.api.model.Puesto;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PuestoRepository
        extends JpaRepository<Puesto, Long> {

    // Recuperar los puestos junto con sus empresas,
    // ordenados alfabéticamente por nombre.

    @Query("""
            SELECT p
            FROM Puesto p
            JOIN FETCH p.empresa
            ORDER BY p.nombre ASC
            """)
    List<Puesto> buscarPuestosDisponibles();


    // Recuperar un puesto junto con su empresa,
    // sus características y las competencias asociadas.

    @Query("""
            SELECT DISTINCT p
            FROM Puesto p
            JOIN FETCH p.empresa
            LEFT JOIN FETCH p.caracteristicas cp
            LEFT JOIN FETCH cp.competencia
            WHERE p.codigo = :codigo
            """)
    Optional<Puesto> buscarPorCodigoConCompetencias(
            @Param("codigo") String codigo
    );

}