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

    // El codigo tiene que tener al menos un caracter que no sea digito;
    // los valores solo numericos van a /{id} (ver buscarPuesto)
    @GetMapping("/{codigo:.*\\D.*}")
    public ResponseEntity<DetallePuestoDTO>
            obtenerDetallePuesto(
                    @PathVariable("codigo") String codigo) {

        DetallePuestoDTO puesto =
                puestoService.obtenerDetallePuesto(codigo);

        return ResponseEntity.ok(puesto);

    }


    // ==========================================
    // BUSCAR UN PUESTO POR SU ID
    // ==========================================

    // El {id:\d+} solo acepta numeros, asi no choca con /{codigo}
    @GetMapping("/{id:\\d+}")
    public ResponseEntity<DetallePuestoDTO>
            buscarPuesto(
                    @PathVariable("id") Long id) {

        DetallePuestoDTO puesto =
                puestoService.buscarPuesto(id);

        return ResponseEntity.ok(puesto);

    }

}