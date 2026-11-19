package ar.edu.utn.frsf4.api.model;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Entity
@Table(
    uniqueConstraints = {
        @UniqueConstraint(
            columnNames = {"puesto_id", "competencia_id"}
        )
    }
)
@Getter
@Setter
@NoArgsConstructor
public class CaracteristicaPuesto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer ponderacion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "puesto_id", nullable = false)
    private Puesto puesto;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "competencia_id", nullable = false)
    private Competencia competencia;

    public CaracteristicaPuesto(
            Integer ponderacion,
            Puesto puesto,
            Competencia competencia) {

        this.ponderacion = ponderacion;
        this.puesto = puesto;
        this.competencia = competencia;

    }

}