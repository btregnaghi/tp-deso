package ar.edu.utn.frsf4.api.controller;

import ar.edu.utn.frsf4.api.dto.ValidarCandidatosRequestDTO;
import ar.edu.utn.frsf4.api.dto.ValidarCandidatosResponseDTO;

import ar.edu.utn.frsf4.api.service.CuestionarioService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cuestionarios")
public class CuestionarioController {

    private final CuestionarioService cuestionarioService;

    public CuestionarioController(
            CuestionarioService cuestionarioService) {

        this.cuestionarioService = cuestionarioService;
    }

    @PostMapping("/validar-candidatos")
    public ResponseEntity<ValidarCandidatosResponseDTO>
            validarCandidatos(
                @RequestBody ValidarCandidatosRequestDTO solicitud) {

        ValidarCandidatosResponseDTO respuesta =
                cuestionarioService.validarCandidatos(
                    solicitud.getIdsCandidatos()
                );

        return ResponseEntity.ok(respuesta);
    }

}