
package ar.edu.utn.frsf4.api.controller;

import ar.edu.utn.frsf4.api.dto.CandidatoDTO;
import ar.edu.utn.frsf4.api.service.CandidatoService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/candidatos")
public class CandidatoController {

    private final CandidatoService candidatoService;

    public CandidatoController(CandidatoService candidatoService) {
        this.candidatoService = candidatoService;
    }

    @GetMapping
    public List<CandidatoDTO> buscarCandidatos(
            @RequestParam(required = false) String apellido,
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) Integer nroCandidato) {

        return candidatoService.buscarCandidatos(
                apellido,
                nombre,
                nroCandidato
        );
    }
}
