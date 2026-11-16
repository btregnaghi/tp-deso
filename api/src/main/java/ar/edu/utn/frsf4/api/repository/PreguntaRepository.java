package ar.edu.utn.frsf4.api.repository;

import ar.edu.utn.frsf4.api.model.Pregunta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PreguntaRepository extends JpaRepository<Pregunta, Long> {

    List<Pregunta> findByFechaBajaIsNull();

    @Query("""
        SELECT p FROM Pregunta p
        WHERE p.fechaBaja IS NULL
        AND (:competencia IS NULL OR
             LOWER(p.factor.competencia.nombre)
             LIKE LOWER(CONCAT('%', :competencia, '%')))
        AND (:factor IS NULL OR
             LOWER(p.factor.nombre)
             LIKE LOWER(CONCAT('%', :factor, '%')))
        AND (:nombre IS NULL OR
             LOWER(p.nombre)
             LIKE LOWER(CONCAT('%', :nombre, '%')))
        """)
    List<Pregunta> buscarPorCriterios(
        @Param("competencia") String competencia,
        @Param("factor") String factor,
        @Param("nombre") String nombre
    );
}