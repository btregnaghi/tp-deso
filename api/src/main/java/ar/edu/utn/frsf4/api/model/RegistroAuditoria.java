package ar.edu.utn.frsf4.api.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Getter
@NoArgsConstructor
public class RegistroAuditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate fechaEliminacion;

    @Column(nullable = false)
    private LocalTime horaEliminacion;

    @Column(nullable = false)
    private String usuarioResponsable;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pregunta_id", nullable = false)
    private Pregunta pregunta;

    public RegistroAuditoria(
            LocalDate fechaEliminacion,
            LocalTime horaEliminacion,
            String usuarioResponsable,
            Pregunta pregunta) {

        this.fechaEliminacion = fechaEliminacion;
        this.horaEliminacion = horaEliminacion;
        this.usuarioResponsable = usuarioResponsable;
        this.pregunta = pregunta;
    }
}