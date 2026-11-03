package ar.edu.utn.frsf4.api.model;

import ar.edu.utn.frsf4.api.model.enums.TipoPregunta;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Pregunta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // versión de negocio (no es @Version de JPA): sube cuando se modifica una pregunta ya usada
    @Column(nullable = false)
    private Integer version;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false, length = 1000)
    private String texto;

    @Column(length = 500)
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoPregunta tipo;

    private LocalDateTime fechaModificacion;

    // null mientras la pregunta está activa (baja lógica)
    private LocalDate fechaBaja;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "factor_id", nullable = false)
    private Factor factor;

    // composición: las opciones nacen y mueren con la pregunta
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "pregunta_id", nullable = false)
    private List<Opcion> opciones = new ArrayList<>();

    public Pregunta(String nombre, String texto, String descripcion, TipoPregunta tipo, Factor factor) {
        this.version = 1;
        this.nombre = nombre;
        this.texto = texto;
        this.descripcion = descripcion;
        this.tipo = tipo;
        this.factor = factor;
        this.fechaModificacion = LocalDateTime.now();
    }

    public void agregarOpcion(Opcion opcion) {
        this.opciones.add(opcion);
    }
}
