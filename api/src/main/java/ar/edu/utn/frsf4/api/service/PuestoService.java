package ar.edu.utn.frsf4.api.service;

import ar.edu.utn.frsf4.api.dto.PuestoDTO;
import ar.edu.utn.frsf4.api.dto.DetallePuestoDTO;
import ar.edu.utn.frsf4.api.dto.CompetenciaPonderadaDTO;

import ar.edu.utn.frsf4.api.model.Puesto;
import ar.edu.utn.frsf4.api.model.Competencia;
import ar.edu.utn.frsf4.api.model.CaracteristicaPuesto;

import ar.edu.utn.frsf4.api.repository.PuestoRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Comparator;
import java.util.NoSuchElementException;

@Service
public class PuestoService {

    private final PuestoRepository puestoRepository;

    public PuestoService(PuestoRepository puestoRepository) {

        this.puestoRepository = puestoRepository;

    }


    // ==========================================
    // OBTENER LOS PUESTOS DISPONIBLES
    // ==========================================

    @Transactional(readOnly = true)
    public List<PuestoDTO> obtenerPuestosDisponibles() {

        return puestoRepository.buscarPuestosDisponibles()
                .stream()
                .map(this::convertirAPuestoDTO)
                .toList();

    }


    // ==========================================
    // OBTENER EL DETALLE DE UN PUESTO
    // ==========================================

    @Transactional(readOnly = true)
    public DetallePuestoDTO obtenerDetallePuesto(String codigo) {

        // Validar el código recibido.

        if (codigo == null || codigo.isBlank()) {

            throw new IllegalArgumentException(
                    "Debe seleccionar una función laboral."
            );

        }

        String codigoLimpio = codigo.trim();

        // Recuperar el puesto y sus competencias.

        Puesto puesto = puestoRepository
                .buscarPorCodigoConCompetencias(codigoLimpio)
                .orElseThrow(() -> new NoSuchElementException(
                        "No existe un puesto con el código indicado."
                ));

        // Convertir el puesto al DTO correspondiente.

        return convertirADetallePuestoDTO(puesto);

    }


    // ==========================================
    // BUSCAR UN PUESTO POR SU ID
    // ==========================================

    @Transactional(readOnly = true)
    public DetallePuestoDTO buscarPuesto(Long id) {

        // Validar el id recibido.

        if (id == null || id <= 0) {

            throw new IllegalArgumentException(
                    "Debe seleccionar una función laboral."
            );

        }

        // Recuperar el puesto y sus competencias.

        Puesto puesto = puestoRepository
                .buscarPorIdConCompetencias(id)
                .orElseThrow(() -> new NoSuchElementException(
                        "No existe un puesto con el id indicado."
                ));

        // Convertir el puesto al DTO correspondiente.

        return convertirADetallePuestoDTO(puesto);

    }


    // ==========================================
    // CONVERSIÓN A PUESTODTO
    // ==========================================

    private PuestoDTO convertirAPuestoDTO(Puesto puesto) {

        return new PuestoDTO(

                puesto.getId(),
                puesto.getCodigo(),
                puesto.getNombre(),
                puesto.getEmpresa().getNombre()

        );

    }


    // ==========================================
    // CONVERSIÓN A DETALLEPUESTODTO
    // ==========================================

    private DetallePuestoDTO convertirADetallePuestoDTO(
            Puesto puesto) {

        // Convertir las características del puesto
        // en competencias ponderadas.

        List<CompetenciaPonderadaDTO> competencias =
                puesto.getCaracteristicas()
                        .stream()
                        .map(this::convertirACompetenciaPonderadaDTO)
                        .sorted(Comparator.comparing(
                                CompetenciaPonderadaDTO::getNombre
                        ))
                        .toList();

        return new DetallePuestoDTO(

                puesto.getCodigo(),
                puesto.getNombre(),
                puesto.getEmpresa().getNombre(),
                competencias

        );

    }


    // ==========================================
    // CONVERSIÓN A COMPETENCIAPONDERADADTO
    // ==========================================

    private CompetenciaPonderadaDTO
            convertirACompetenciaPonderadaDTO(
                    CaracteristicaPuesto caracteristica) {

        Competencia competencia =
                caracteristica.getCompetencia();

        return new CompetenciaPonderadaDTO(

                competencia.getId(),
                competencia.getCodigo(),
                competencia.getNombre(),
                caracteristica.getPonderacion()

        );

    }

}