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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoCuestionario estado;

    @Column(nullable = false)
    private LocalDateTime fechaCreacion;

    private LocalDateTime fechaInicio;

    private LocalDateTime horaFinBloqueoActivo;

    @Column(nullable = false)
    private Integer tiempoMaximo;

    @Column(nullable = false)
    private Integer cantPreguntasPorFactor;

    @Column(nullable = false)
    private Integer cantPreguntasPorBloque;

    private Double puntajeFinal;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "candidato_id", nullable = false)
    private Candidato candidato;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "puesto_id", nullable = false)
    private Puesto puesto;

    public Cuestionario(
            Candidato candidato,
            Puesto puesto,
            Integer tiempoMaximo,
            Integer cantPreguntasPorFactor,
            Integer cantPreguntasPorBloque) {

        this.candidato = candidato;
        this.puesto = puesto;
        this.tiempoMaximo = tiempoMaximo;
        this.cantPreguntasPorFactor = cantPreguntasPorFactor;
        this.cantPreguntasPorBloque = cantPreguntasPorBloque;

        this.estado = EstadoCuestionario.ACTIVO;
        this.fechaCreacion = LocalDateTime.now();

    }

    public boolean estaVigente() {

        return this.estado == EstadoCuestionario.ACTIVO
                || this.estado == EstadoCuestionario.EN_PROCESO;

    }

}