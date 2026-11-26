package ar.edu.utn.frsf4.api.repository;

import ar.edu.utn.frsf4.api.model.Pregunta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface PreguntaRepository
        extends JpaRepository<Pregunta, Long> {

    List<Pregunta> findByFechaBajaIsNull();

    // CU026, paso 3: preguntas del factor tal como estaban al crear la evaluacion
    // (fechaReferencia = evaluacion.fechaCreacion). Asi todos los cuestionarios de una
    // evaluacion sortean del mismo banco aunque los candidatos ingresen en dias distintos.
    // Trae las opciones en la misma consulta porque el paso 3 las usa para armar los ItemOpcion.
    @Query("""
        SELECT DISTINCT p FROM Pregunta p
        LEFT JOIN FETCH p.opciones
        WHERE p.factor.id = :idFactor
        AND (p.fechaModificacion IS NULL OR p.fechaModificacion <= :fechaReferencia)
        AND (p.fechaBaja IS NULL OR p.fechaBaja > :fechaReferencia)
        """)
    List<Pregunta> buscarActivasPorFactor(
        @Param("idFactor") Long idFactor,
        @Param("fechaReferencia") LocalDateTime fechaReferencia
    );

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