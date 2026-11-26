package ar.edu.utn.frsf4.api.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter 
@Setter 
@NoArgsConstructor 
@Entity 
public class Bloque {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer nroBloque;

    // composición unidireccional (como Pregunta -> Opcion): la FK bloque_id vive en la tabla item_pregunta
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "bloque_id", nullable = false)
    @OrderBy("ordenVisualizacion")
    private List<ItemPregunta> itemsPregunta = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cuestionario_id", nullable = false)
    private Cuestionario cuestionario;

    public Bloque(Integer nroBloque){
        this.nroBloque = nroBloque;
    }

    public void agregarItemPregunta(ItemPregunta item) {
        this.itemsPregunta.add(item);
    }

    // Un bloque esta respondido cuando todas sus preguntas lo estan
    public boolean estaRespondido() {
        return this.itemsPregunta.stream()
                .allMatch(ItemPregunta::estaRespondida);
    }

}
