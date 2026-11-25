package ar.edu.utn.frsf4.api.service;

import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ar.edu.utn.frsf4.api.dto.CompetenciaDTO;
import ar.edu.utn.frsf4.api.model.Puesto;
import ar.edu.utn.frsf4.api.repository.CompetenciaRepository;
import ar.edu.utn.frsf4.api.repository.PuestoRepository;

@Service 
public class EvaluacionService {
    private final PuestoRepository puestoRepository;

    private final CompetenciaRepository competenciaRepository;

    public EvaluacionService(PuestoRepository puestoRepository, CompetenciaRepository competenciaRepository){
        this.puestoRepository = puestoRepository;
        this.competenciaRepository = competenciaRepository;
    }

    @Transactional(readOnly = true)
    public List<CompetenciaDTO> validarCompetenciasEvaluables(Long idPuesto){
        
        if(idPuesto == null || idPuesto <= 0){
            throw new IllegalArgumentException("Debe seleccionar una función laboral" );
        }

        Puesto puesto = puestoRepository.buscarPorIdConCompetencias(idPuesto)
                            .orElseThrow(() -> new NoSuchElementException("No existe un puesto con ese Id"));

        Set<Long> idsValidos = new HashSet<>(competenciaRepository.buscarIdsCompetenciasValidas());

        return puesto.getCaracteristicas().stream()
                .map(caracteristica -> caracteristica.getCompetencia())
                .filter(competencia -> !idsValidos.contains(competencia.getId()))
                .map(competencia -> new CompetenciaDTO(competencia.getId(), competencia.getNombre()))
                .toList();

    }
}
