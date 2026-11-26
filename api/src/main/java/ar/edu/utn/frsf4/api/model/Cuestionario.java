package ar.edu.utn.frsf4.api.model;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import ar.edu.utn.frsf4.api.model.enums.EstadoCuestionario;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Cuestionario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String claveIngreso;

    // null hasta que el candidato ingresa por primera vez
    private LocalDateTime fechaHoraInicio;

    // null hasta que el cuestionario se completa o se cierra
    private LocalDateTime fechaHoraFin;

    private LocalDateTime ultimoIngreso;

    @Column(nullable = false)
    private Integer cantidadAccesos;

    // null hasta que el cuestionario se completa
    private Double puntajeTotal;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoCuestionario estado;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "evaluacion_id", nullable = false)
    private Evaluacion evaluacion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "candidato_id", nullable = false)
    private Candidato candidato;

    @OneToMany(mappedBy = "cuestionario", cascade= CascadeType.ALL, orphanRemoval= true)
    @OrderBy("nroBloque")
    private List<Bloque> bloques = new ArrayList<>();

    @OneToMany(mappedBy = "cuestionario", cascade= CascadeType.ALL, orphanRemoval= true)
    private List<PuntajeCompetencia> puntajesCompetencia = new ArrayList<>();

    // La evaluacion se asigna con Evaluacion.agregarCuestionario(...)
    public Cuestionario(
            Candidato candidato,
            String claveIngreso,
            EstadoCuestionario estado) {

        this.candidato = candidato;
        this.claveIngreso = claveIngreso;
        this.estado = estado;
        this.cantidadAccesos = 0;

    }

    public boolean estaVigente() {

        return this.estado == EstadoCuestionario.ACTIVO
                || this.estado == EstadoCuestionario.EN_PROCESO;

    }

    // relación bidireccional: hay que cargar los dos lados, si no bloque.cuestionario_id queda null
    public void agregarBloque(Bloque bloque){ 
        this.bloques.add(bloque);
        bloque.setCuestionario(this);
    }

    public void agregarPuntajeCompetencia(PuntajeCompetencia puntajeCompetencia){
        this.puntajesCompetencia.add(puntajeCompetencia);
        puntajeCompetencia.setCuestionario(this);
    }

    public void registrarIngreso(LocalDateTime ahora){
        this.cantidadAccesos+=1;
        this.ultimoIngreso = ahora;
    }

    // Paso 3 del CU026: solo un cuestionario ACTIVO puede empezar a responderse
    public void iniciar(LocalDateTime ahora){
        if (this.estado != EstadoCuestionario.ACTIVO) {
            throw new IllegalStateException(
                "Solo se puede iniciar un cuestionario en estado ACTIVO."
            );
        }
        this.estado = EstadoCuestionario.EN_PROCESO;
        this.fechaHoraInicio = ahora;
    }

    // Lo cierra en uno de los estados finales; un cuestionario ya cerrado no se vuelve a cerrar
    public void finalizar(EstadoCuestionario estadoCuestionario, LocalDateTime ahora){
        if (!estaVigente()) {
            throw new IllegalStateException(
                "El cuestionario ya se encuentra finalizado."
            );
        }
        if (estadoCuestionario != EstadoCuestionario.COMPLETADO
                && estadoCuestionario != EstadoCuestionario.INCOMPLETO
                && estadoCuestionario != EstadoCuestionario.SIN_CONTESTAR) {
            throw new IllegalArgumentException(
                "Un cuestionario solo puede finalizar como COMPLETADO, INCOMPLETO o SIN_CONTESTAR."
            );
        }
        this.estado = estadoCuestionario;
        this.fechaHoraFin = ahora;
    }

    // Flujo 1.C: primer bloque (por nroBloque, gracias al @OrderBy) que todavia tiene preguntas sin responder.
    // Vacio si ya respondio todos los bloques.
    public Optional<Integer> obtenerBloqueSinResponder(){
        return this.bloques.stream()
                .filter(bloque -> !bloque.estaRespondido())
                .map(Bloque::getNroBloque)
                .findFirst();
    }

    // Flujo 1.A: el candidato ya empezo y paso la duracion de la evaluacion
    public boolean superoTiempoMaximo(LocalDateTime ahora){
        if (this.fechaHoraInicio == null) {
            return false;
        }
        LocalDateTime limite = this.fechaHoraInicio
                .plusMinutes(this.evaluacion.getDuracionEnMinutos());
        return ahora.isAfter(limite);
    }

    // Flujo 1.B: paso la fecha de cierre de la evaluacion (el dia de cierre todavia vale)
    public boolean estaVencido(LocalDateTime ahora){
        LocalDate fechaCierre = this.evaluacion.getFechaCierre();
        return fechaCierre != null
                && ahora.toLocalDate().isAfter(fechaCierre);
    }
}
