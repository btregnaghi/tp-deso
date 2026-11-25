package ar.edu.utn.frsf4.api.service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Value;

import ar.edu.utn.frsf4.api.dto.ClaveEvaluacionCandidatoDTO;
import ar.edu.utn.frsf4.api.dto.CompetenciaDTO;
import ar.edu.utn.frsf4.api.model.Candidato;
import ar.edu.utn.frsf4.api.model.Cuestionario;
import ar.edu.utn.frsf4.api.model.Evaluacion;
import ar.edu.utn.frsf4.api.model.Puesto;
import ar.edu.utn.frsf4.api.model.enums.EstadoCuestionario;
import ar.edu.utn.frsf4.api.repository.CandidatoRepository;
import ar.edu.utn.frsf4.api.repository.CompetenciaRepository;
import ar.edu.utn.frsf4.api.repository.CuestionarioRepository;
import ar.edu.utn.frsf4.api.repository.EvaluacionRepository;
import ar.edu.utn.frsf4.api.repository.PuestoRepository;

/**
 * Lógica de negocio del CU025 - Evaluar candidatos.
 */
@Service
public class EvaluacionService {

    private final PuestoRepository puestoRepository;

    private final CompetenciaRepository competenciaRepository;

    private final CandidatoRepository candidatoRepository;

    private final EvaluacionRepository evaluacionRepository;

    private final CuestionarioRepository cuestionarioRepository;

    // Parámetros del sistema, definidos en application.properties (CU026)
    private final Integer duracionEnMinutos;

    private final Integer cantPreguntasPorBloque;

    // Sin 0/O ni 1/l/I: se confunden al dictar la clave por teléfono
    private static final String CARACTERES = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz23456789";

    private static final int LONGITUD_CLAVE = 8;

    // SecureRandom para que las claves no sean predecibles
    private static final SecureRandom RANDOM = new SecureRandom();

    public EvaluacionService(PuestoRepository puestoRepository,
            CompetenciaRepository competenciaRepository,
            CandidatoRepository candidatoRepository,
            EvaluacionRepository evaluacionRepository,
            CuestionarioRepository cuestionarioRepository,
            @Value("${evaluacion.duracion-en-minutos}") Integer duracionEnMinutos,
            @Value("${evaluacion.cant-preguntas-por-bloque}") Integer cantPreguntasPorBloque) {
        this.puestoRepository = puestoRepository;
        this.competenciaRepository = competenciaRepository;
        this.candidatoRepository = candidatoRepository;
        this.evaluacionRepository = evaluacionRepository;
        this.duracionEnMinutos = duracionEnMinutos;
        this.cantPreguntasPorBloque = cantPreguntasPorBloque;
        this.cuestionarioRepository = cuestionarioRepository;
    }

    /**
     * CU025 paso 9: competencias del puesto que no se pueden evaluar.
     * Una lista vacía significa que todas son evaluables.
     */
    @Transactional(readOnly = true)
    public List<CompetenciaDTO> validarCompetenciasEvaluables(Long idPuesto) {

        if (idPuesto == null || idPuesto <= 0) {
            throw new IllegalArgumentException("Debe seleccionar una función laboral");
        }

        Puesto puesto = puestoRepository.buscarPorIdConCompetencias(idPuesto)
                .orElseThrow(() -> new NoSuchElementException("No existe un puesto con ese Id"));

        // Set para que cada contains() sea inmediato
        Set<Long> idsValidos = new HashSet<>(competenciaRepository.buscarIdsCompetenciasValidas());

        return puesto.getCaracteristicas().stream()
                .map(caracteristica -> caracteristica.getCompetencia())
                .filter(competencia -> !idsValidos.contains(competencia.getId()))
                .map(competencia -> new CompetenciaDTO(competencia.getId(), competencia.getNombre()))
                .toList();

    }

    /**
     * Clave de acceso aleatoria de 8 caracteres (requerimiento del CU025).
     */
    private String generarClaveAcceso() {
        StringBuilder clave = new StringBuilder(LONGITUD_CLAVE);

        for (int i = 0; i < LONGITUD_CLAVE; i++) {
            int posicion = RANDOM.nextInt(CARACTERES.length());
            clave.append(CARACTERES.charAt(posicion));
        }

        return clave.toString();
    }

    /**
     * CU025 pasos 12 y 13: crea la evaluación con un cuestionario ACTIVO
     * por candidato y devuelve las claves de acceso.
     */
    @Transactional
    public List<ClaveEvaluacionCandidatoDTO> crearEvaluacion(Long idPuesto, List<Long> idsCandidatos) {

        if (idPuesto == null || idPuesto <= 0) {
            throw new IllegalArgumentException("Debe seleccionar una función laboral");
        }

        if (idsCandidatos == null || idsCandidatos.isEmpty()) {
            throw new IllegalArgumentException("Debe seleccionar uno o más candidatos");
        }

        Puesto puesto = puestoRepository.findById(idPuesto)
                .orElseThrow(() -> new NoSuchElementException("No existe un puesto con ese Id"));

        // Sin repetidos, para que la comparación de tamaños sea correcta
        List<Long> idsUnicos = idsCandidatos.stream().distinct().toList();
        List<Candidato> candidatos = candidatoRepository.findAllById(idsUnicos);

        if (candidatos.size() != idsUnicos.size()) {
            throw new IllegalArgumentException("Uno o más candidatos seleccionados no existen");
        }

        // Se repite la validación del paso 6 por si llaman al endpoint sin pasar por el front
        List<Candidato> bloqueados = cuestionarioRepository.buscarCandidatosConCuestionariosVigentes(idsUnicos, List.of(EstadoCuestionario.ACTIVO, EstadoCuestionario.EN_PROCESO));

        if (!bloqueados.isEmpty()) {
            String apellidos = bloqueados.stream()
                    .map(Candidato::getApellido)
                    .collect(Collectors.joining(", "));

            throw new IllegalStateException("Los siguientes candidatos poseen cuestionarios activos o en proceso: " + apellidos);
        }

        // Ej: EV-DEV01-20261125-1830
        String codigo = "EV-" + puesto.getCodigo() + "-"
                + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmm"));

        Evaluacion evaluacion = new Evaluacion(codigo, puesto, this.duracionEnMinutos, this.cantPreguntasPorBloque);

        for (Candidato candidato : candidatos) {
            Cuestionario cuestionario = new Cuestionario(candidato, generarClaveAcceso(), EstadoCuestionario.ACTIVO);
            evaluacion.agregarCuestionario(cuestionario);
        }

        // El cascade guarda también los cuestionarios
        evaluacionRepository.save(evaluacion);

        return evaluacion.getCuestionarios().stream()
            .map(cuestionario -> new ClaveEvaluacionCandidatoDTO(
                cuestionario.getCandidato().getNroCandidato(),
                cuestionario.getCandidato().getNombre(),
                cuestionario.getCandidato().getApellido(),
                cuestionario.getClaveIngreso()))
            .toList();

    }
}
