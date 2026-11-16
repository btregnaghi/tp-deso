package ar.edu.utn.frsf4.api.controller;

import ar.edu.utn.frsf4.api.dto.PreguntaDTO;
import ar.edu.utn.frsf4.api.service.PreguntaService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/preguntas")
public class PreguntaController {

    private final PreguntaService preguntaService;

    public PreguntaController(PreguntaService preguntaService) {
        this.preguntaService = preguntaService;
    }

    @GetMapping
    public List<PreguntaDTO> buscarPreguntas(
            @RequestParam(required = false) String competencia,
            @RequestParam(required = false) String factor,
            @RequestParam(required = false) String nombre) {

        return preguntaService.buscarPreguntas(
            competencia,
            factor,
            nombre
        );
    }
}