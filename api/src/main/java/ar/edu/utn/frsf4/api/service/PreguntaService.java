package ar.edu.utn.frsf4.api.service;

import ar.edu.utn.frsf4.api.model.Pregunta;
import ar.edu.utn.frsf4.api.repository.PreguntaRepository;
import org.springframework.stereotype.Service;

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
}
