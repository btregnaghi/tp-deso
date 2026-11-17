package ar.edu.utn.frsf4.api.controller;

import ar.edu.utn.frsf4.api.dto.CompetenciaDTO;
import ar.edu.utn.frsf4.api.service.CompetenciaService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/competencias")
public class CompetenciaController {

    private final CompetenciaService competenciaService;

    public CompetenciaController(
            CompetenciaService competenciaService) {
        this.competenciaService = competenciaService;
    }

    @GetMapping
    public List<CompetenciaDTO> obtenerCompetenciasActivas() {
        return competenciaService.obtenerCompetenciasActivas();
    }
}