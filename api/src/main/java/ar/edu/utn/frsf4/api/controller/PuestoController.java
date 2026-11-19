package ar.edu.utn.frsf4.api.controller;

import ar.edu.utn.frsf4.api.dto.PuestoDTO;
import ar.edu.utn.frsf4.api.dto.DetallePuestoDTO;

import ar.edu.utn.frsf4.api.service.PuestoService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/puestos")
public class PuestoController {

    private final PuestoService puestoService;

    public PuestoController(PuestoService puestoService) {

        this.puestoService = puestoService;

    }


    // ==========================================
    // OBTENER LOS PUESTOS DISPONIBLES
    // ==========================================

    @GetMapping
    public ResponseEntity<List<PuestoDTO>>
            obtenerPuestosDisponibles() {

        List<PuestoDTO> puestos =
                puestoService.obtenerPuestosDisponibles();

        return ResponseEntity.ok(puestos);

    }


    // ==========================================
    // OBTENER EL DETALLE DE UN PUESTO
    // ==========================================

    @GetMapping("/{codigo}")
    public ResponseEntity<DetallePuestoDTO>
            obtenerDetallePuesto(
                    @PathVariable("codigo") String codigo) {

        DetallePuestoDTO puesto =
                puestoService.obtenerDetallePuesto(codigo);

        return ResponseEntity.ok(puesto);

    }

}