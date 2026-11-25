package ar.edu.utn.frsf4.api.service;

import ar.edu.utn.frsf4.api.dto.CompetenciaDTO;
import ar.edu.utn.frsf4.api.repository.CompetenciaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CompetenciaService {

    private final CompetenciaRepository competenciaRepository;

    public CompetenciaService(
            CompetenciaRepository competenciaRepository) {
        this.competenciaRepository = competenciaRepository;
    }

    @Transactional(readOnly = true)
    public List<CompetenciaDTO> obtenerCompetenciasActivas() {

        return competenciaRepository.findByFechaBajaIsNull()
                .stream()
                .map(competencia -> new CompetenciaDTO(
                        competencia.getId(),
                        competencia.getNombre()
                ))
                .toList();
    }
}