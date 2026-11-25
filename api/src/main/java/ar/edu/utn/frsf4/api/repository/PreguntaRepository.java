package ar.edu.utn.frsf4.api.repository;

import ar.edu.utn.frsf4.api.model.Pregunta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PreguntaRepository
        extends JpaRepository<Pregunta, Long> {

    List<Pregunta> findByFechaBajaIsNull();

    @Query("""
        SELECT p FROM Pregunta p
        WHERE p.fechaBaja IS NULL

        AND (CAST(:competencia AS String) IS NULL OR
             LOWER(FUNCTION('unaccent', p.factor.competencia.nombre))
             LIKE LOWER(FUNCTION('unaccent',
             CONCAT('%', CAST(:competencia AS String), '%'))))

        AND (CAST(:factor AS String) IS NULL OR
             LOWER(FUNCTION('unaccent', p.factor.nombre))
             LIKE LOWER(FUNCTION('unaccent',
             CONCAT('%', CAST(:factor AS String), '%'))))

        AND (CAST(:nombre AS String) IS NULL OR
             LOWER(FUNCTION('unaccent', p.nombre))
             LIKE LOWER(FUNCTION('unaccent',
             CONCAT('%', CAST(:nombre AS String), '%'))))
        """)
    List<Pregunta> buscarPorCriterios(
        @Param("competencia") String competencia,
        @Param("factor") String factor,
        @Param("nombre") String nombre
    );
}