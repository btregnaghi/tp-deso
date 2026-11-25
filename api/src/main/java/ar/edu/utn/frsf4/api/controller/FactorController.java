package ar.edu.utn.frsf4.api.controller;

import ar.edu.utn.frsf4.api.dto.FactorDTO;
import ar.edu.utn.frsf4.api.service.FactorService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/factores")
public class FactorController {

    private final FactorService factorService;

    public FactorController(FactorService factorService) {
        this.factorService = factorService;
    }

    @GetMapping
    public List<FactorDTO> obtenerFactoresActivos() {
        return factorService.obtenerFactoresActivos();
    }
}