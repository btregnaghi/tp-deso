package ar.edu.utn.frsf4.api.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import ar.edu.utn.frsf4.api.model.enums.TipoPregunta;
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

    // Un item esta respondido si el candidato marco al menos una de sus opciones
    public boolean estaRespondida(){
        return this.itemsOpcion.stream()
                .anyMatch(ItemOpcion::getRespondido);
    }

    // Paso 7 del CU026. Recibe ids de ItemOpcion (no de Opcion): marca las elegidas y desmarca el resto,
    // asi que volver a responder reemplaza la respuesta anterior.
    // Una lista vacia deja la pregunta sin responder (pasa si el temporizador llega a 00:00).
    public void responder(List<Long> idsOpcionesSeleccionadas){
        Set<Long> seleccionadas = idsOpcionesSeleccionadas == null
                ? Set.of()
                : new HashSet<>(idsOpcionesSeleccionadas);

        Set<Long> idsPropios = this.itemsOpcion.stream()
                .map(ItemOpcion::getId)
                .collect(Collectors.toSet());

        if (!idsPropios.containsAll(seleccionadas)) {
            throw new IllegalArgumentException(
                "Se seleccionaron opciones que no pertenecen a la pregunta."
            );
        }

        if (this.pregunta.getTipo() == TipoPregunta.OPCION_SIMPLE
                && seleccionadas.size() > 1) {
            throw new IllegalArgumentException(
                "La pregunta admite una sola respuesta."
            );
        }

        this.itemsOpcion.forEach(itemOpcion ->
            itemOpcion.setRespondido(seleccionadas.contains(itemOpcion.getId())));
    }

    // Observacion (3) del CU026: suma las ponderaciones de las opciones que marco el candidato
    public Double calcularPuntajeObtenido(){
        this.puntajeObtenido = this.itemsOpcion.stream()
                .filter(ItemOpcion::getRespondido)
                .mapToDouble(itemOpcion -> itemOpcion.getOpcion().getPonderacion())
                .sum();
        return this.puntajeObtenido;
    }
}
