package ar.edu.utn.frsf4.api.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ar.edu.utn.frsf4.api.dto.CompetenciaDTO;
import ar.edu.utn.frsf4.api.service.EvaluacionService;

@RestController 
@RequestMapping("/api/evaluaciones") 
public class EvaluacionController {

    private final EvaluacionService evaluacionService;

    public EvaluacionController(EvaluacionService evaluacionService){
        this.evaluacionService = evaluacionService;
    }

    @GetMapping("/competencias-validas")
    public ResponseEntity<List<CompetenciaDTO>> validarCompetenciasEvaluables(@RequestParam("idPuesto") Long idPuesto){
        List<CompetenciaDTO> competencias = evaluacionService.validarCompetenciasEvaluables(idPuesto);
        
        return ResponseEntity.ok(competencias);
    }

}
