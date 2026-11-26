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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity 
@Getter 
@Setter 
@NoArgsConstructor 
public class ItemPregunta {

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable= false)
    private Integer ordenVisualizacion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pregunta_id", nullable = false)
    private Pregunta pregunta;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "item_pregunta_id", nullable = false)
    private List<ItemOpcion> itemsOpcion = new ArrayList<>();

    @Column(nullable= true)
    private Double puntajeObtenido;

    public ItemPregunta(Pregunta pregunta, Integer ordenVisualizacion){
        this.ordenVisualizacion = ordenVisualizacion;
        this.pregunta = pregunta;
    }

    public void agregarItemOpcion(ItemOpcion itemOpcion){
        this.itemsOpcion.add(itemOpcion);
    }
}
