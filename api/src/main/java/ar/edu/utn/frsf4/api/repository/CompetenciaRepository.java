package ar.edu.utn.frsf4.api.repository;

import ar.edu.utn.frsf4.api.model.Competencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface CompetenciaRepository extends JpaRepository<Competencia, Long> {

    List<Competencia> findByFechaBajaIsNull();

    // Una competencia es evaluable si tiene al menos un factor activo
    // con 2 o mas preguntas activas (CU025, paso 9).
    @Query("""
            SELECT DISTINCT c.id
            FROM Pregunta p
            JOIN p.factor f
            JOIN f.competencia c
            WHERE p.fechaBaja IS NULL
              AND f.fechaBaja IS NULL
              AND c.fechaBaja IS NULL
            GROUP BY f.id, c.id
            HAVING COUNT(p) >= 2
            """)
    List<Long> buscarIdsCompetenciasValidas();

}
