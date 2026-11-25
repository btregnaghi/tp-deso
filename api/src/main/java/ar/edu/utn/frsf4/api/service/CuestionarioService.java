package ar.edu.utn.frsf4.api.service;

import ar.edu.utn.frsf4.api.dto.CandidatoDTO;
import ar.edu.utn.frsf4.api.dto.ValidarCandidatosResponseDTO;

import ar.edu.utn.frsf4.api.model.Candidato;
import ar.edu.utn.frsf4.api.model.enums.EstadoCuestionario;

import ar.edu.utn.frsf4.api.repository.CandidatoRepository;
import ar.edu.utn.frsf4.api.repository.CuestionarioRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CuestionarioService {

    private final CuestionarioRepository cuestionarioRepository;
    private final CandidatoRepository candidatoRepository;

    public CuestionarioService(
            CuestionarioRepository cuestionarioRepository,
            CandidatoRepository candidatoRepository) {

        this.cuestionarioRepository = cuestionarioRepository;
        this.candidatoRepository = candidatoRepository;
    }

    @Transactional(readOnly = true)
    public ValidarCandidatosResponseDTO validarCandidatos(
            List<Long> idsCandidatos) {

        if (idsCandidatos == null || idsCandidatos.isEmpty()) {

            throw new IllegalArgumentException(
                "Debe seleccionar al menos un candidato para evaluar."
            );
        }

        if (idsCandidatos.stream()
                .anyMatch(id -> id == null || id <= 0)) {

            throw new IllegalArgumentException(
                "Se recibieron identificadores de candidatos inválidos."
            );
        }

        List<Long> idsUnicos = idsCandidatos.stream()
                .distinct()
                .toList();

        List<Candidato> candidatos =
                candidatoRepository.findAllById(idsUnicos);

        if (candidatos.size() != idsUnicos.size()) {

            throw new IllegalArgumentException(
                "Uno o más candidatos seleccionados no existen."
            );
        }

        if (candidatos.stream()
                .anyMatch(candidato -> !candidato.estaActivo())) {

            throw new IllegalArgumentException(
                "Uno o más candidatos seleccionados no están habilitados."
            );
        }

        List<EstadoCuestionario> estadosVigentes = List.of(
                EstadoCuestionario.ACTIVO,
                EstadoCuestionario.EN_PROCESO
        );

        List<Candidato> candidatosBloqueados =
                cuestionarioRepository
                    .buscarCandidatosConCuestionariosVigentes(
                        idsUnicos,
                        estadosVigentes
                    );

        List<CandidatoDTO> candidatosBloqueadosDTO =
                candidatosBloqueados.stream()
                    .map(this::convertirADTO)
                    .toList();

        boolean puedeAvanzar =
                candidatosBloqueadosDTO.isEmpty();

        return new ValidarCandidatosResponseDTO(
                puedeAvanzar,
                candidatosBloqueadosDTO
        );
    }

    private CandidatoDTO convertirADTO(Candidato candidato) {

        return new CandidatoDTO(
                candidato.getId(),
                candidato.getNroCandidato(),
                candidato.getApellido(),
                candidato.getNombre(),
                candidato.getTipoDoc(),
                candidato.getNroDoc()
        );
    }

}