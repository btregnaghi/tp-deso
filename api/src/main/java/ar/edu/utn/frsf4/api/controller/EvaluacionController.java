package ar.edu.utn.frsf4.api.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ar.edu.utn.frsf4.api.dto.ClaveEvaluacionCandidatoDTO;
import ar.edu.utn.frsf4.api.dto.CompetenciaDTO;
import ar.edu.utn.frsf4.api.dto.CrearEvaluacionDTO;
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

    @PostMapping
    public ResponseEntity<List<ClaveEvaluacionCandidatoDTO>> crearEvaluacion(
            @RequestBody CrearEvaluacionDTO solicitud){

        List<ClaveEvaluacionCandidatoDTO> claves = evaluacionService.crearEvaluacion(solicitud.getIdPuesto(), solicitud.getIdsCandidatos());
        
         return ResponseEntity.status(HttpStatus.CREATED).body(claves);

     }
}
