package ar.edu.utn.frsf4.api.repository;

import ar.edu.utn.frsf4.api.model.Cuestionario;
import ar.edu.utn.frsf4.api.model.Candidato;
import ar.edu.utn.frsf4.api.model.enums.EstadoCuestionario;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CuestionarioRepository
        extends JpaRepository<Cuestionario, Long> {

    @Query("""
        SELECT DISTINCT c.candidato
        FROM Cuestionario c
        WHERE c.candidato.id IN :idsCandidatos
        AND c.estado IN :estados
        ORDER BY c.candidato.apellido, c.candidato.nombre
        """)
    List<Candidato> buscarCandidatosConCuestionariosVigentes(
            @Param("idsCandidatos") List<Long> idsCandidatos,
            @Param("estados") List<EstadoCuestionario> estados
    );

}