package ar.edu.utn.frsf4.api.service;

import ar.edu.utn.frsf4.api.model.Pregunta;
import ar.edu.utn.frsf4.api.repository.PreguntaRepository;
import org.springframework.stereotype.Service;
import ar.edu.utn.frsf4.api.dto.PreguntaDTO;
import ar.edu.utn.frsf4.api.dto.OpcionDTO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PreguntaService {

    private final PreguntaRepository preguntaRepository;

    public PreguntaService(PreguntaRepository preguntaRepository) {
        this.preguntaRepository = preguntaRepository;
    }

    public List<Pregunta> obtenerPreguntasActivas() {
        return preguntaRepository.findByFechaBajaIsNull();
    }

    @Transactional(readOnly = true)
public List<PreguntaDTO> buscarPreguntas(
        String competencia,
        String factor,
        String nombre) {

    competencia = normalizarCriterio(competencia);
    factor = normalizarCriterio(factor);
    nombre = normalizarCriterio(nombre);

    if (competencia == null && factor == null && nombre == null) {
        throw new IllegalArgumentException(
            "Debe ingresar al menos un criterio de búsqueda."
        );
    }

    List<Pregunta> preguntas =
        preguntaRepository.buscarPorCriterios(
            competencia,
            factor,
            nombre
        );

    return preguntas.stream()
            .map(this::convertirADTO)
            .toList();
}


    private String normalizarCriterio(String criterio) {

        if (criterio == null || criterio.isBlank()) {
            return null;
        }

        return criterio.trim();
    }

    private PreguntaDTO convertirADTO(Pregunta pregunta) {

    PreguntaDTO dto = new PreguntaDTO();

    dto.setId(pregunta.getId());
    dto.setVersion(pregunta.getVersion());
    dto.setNombre(pregunta.getNombre());
    dto.setTexto(pregunta.getTexto());
    dto.setDescripcion(pregunta.getDescripcion());
    dto.setTipo(pregunta.getTipo());
    dto.setFechaModificacion(pregunta.getFechaModificacion());
    dto.setFechaBaja(pregunta.getFechaBaja());

    dto.setFactorId(pregunta.getFactor().getId());
    dto.setFactorNombre(pregunta.getFactor().getNombre());

    dto.setCompetenciaId(
        pregunta.getFactor().getCompetencia().getId()
    );

    dto.setCompetenciaNombre(
        pregunta.getFactor().getCompetencia().getNombre()
    );

    dto.setOpciones(
        pregunta.getOpciones().stream()
            .map(opcion -> new OpcionDTO(
                opcion.getOrdenVisualizacion(),
                opcion.getTexto(),
                opcion.getPonderacion()
            ))
            .toList()
    );

    return dto;
}
}