
package ar.edu.utn.frsf4.api.repository;

import ar.edu.utn.frsf4.api.model.Candidato;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CandidatoRepository
        extends JpaRepository<Candidato, Long> {

    List<Candidato> findByEliminadoFalse();

    @Query("""
        SELECT c FROM Candidato c
        WHERE c.eliminado = false

        AND (CAST(:apellido AS String) IS NULL OR
             LOWER(FUNCTION('unaccent', c.apellido))
             LIKE LOWER(FUNCTION('unaccent',
             CONCAT('%', CAST(:apellido AS String), '%'))))

        AND (CAST(:nombre AS String) IS NULL OR
             LOWER(FUNCTION('unaccent', c.nombre))
             LIKE LOWER(FUNCTION('unaccent',
             CONCAT('%', CAST(:nombre AS String), '%'))))

        AND (CAST(:nroCandidato AS Integer) IS NULL OR
             c.nroCandidato = :nroCandidato)

        ORDER BY c.apellido, c.nombre, c.nroCandidato
        """)
    List<Candidato> buscarPorCriterios(
            @Param("apellido") String apellido,
            @Param("nombre") String nombre,
            @Param("nroCandidato") Integer nroCandidato
    );
}
