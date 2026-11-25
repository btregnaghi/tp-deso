
package ar.edu.utn.frsf4.api.service;

import ar.edu.utn.frsf4.api.dto.CandidatoDTO;
import ar.edu.utn.frsf4.api.model.Candidato;
import ar.edu.utn.frsf4.api.repository.CandidatoRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CandidatoService {

    private final CandidatoRepository candidatoRepository;

    public CandidatoService(CandidatoRepository candidatoRepository) {
        this.candidatoRepository = candidatoRepository;
    }

    @Transactional(readOnly = true)
    public List<CandidatoDTO> buscarCandidatos(
            String apellido,
            String nombre,
            Integer nroCandidato) {

        apellido = normalizarCriterio(apellido);
        nombre = normalizarCriterio(nombre);

        if (apellido == null && nombre == null && nroCandidato == null) {
            throw new IllegalArgumentException(
                "Debe ingresar al menos un criterio de búsqueda."
            );
        }

        if (nroCandidato != null && nroCandidato <= 0) {
            throw new IllegalArgumentException(
                "El número de candidato debe ser mayor que cero."
            );
        }

        List<Candidato> candidatos =
                candidatoRepository.buscarPorCriterios(
                    apellido,
                    nombre,
                    nroCandidato
                );

        return candidatos.stream()
                .map(this::convertirADTO)
                .toList();
    }

    private String normalizarCriterio(String criterio) {

        if (criterio == null || criterio.isBlank()) {
            return null;
        }

        return criterio.trim();
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
