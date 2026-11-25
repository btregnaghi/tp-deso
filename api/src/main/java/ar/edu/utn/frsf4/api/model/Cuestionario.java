package ar.edu.utn.frsf4.api.model;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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

}
