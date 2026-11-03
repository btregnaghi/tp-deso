package ar.edu.utn.frsf4.api.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Opcion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer ordenVisualizacion;

    // puntos que aporta esta opción; las de una pregunta suman 10
    @Column(nullable = false)
    private Integer ponderacion;

    @Column(nullable = false, length = 500)
    private String texto;

    public Opcion(Integer ordenVisualizacion, String texto, Integer ponderacion) {
        this.ordenVisualizacion = ordenVisualizacion;
        this.texto = texto;
        this.ponderacion = ponderacion;
    }
}
