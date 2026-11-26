package ar.edu.utn.frsf4.api.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity 
@Getter 
@Setter 
@NoArgsConstructor
public class Evaluacion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String codigo;

    @Column(nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(nullable = true)
    private LocalDate fechaCierre;

    @Column(nullable = false)
    private Integer duracionEnMinutos;

    @Column(nullable = false)
    private Integer cantPreguntasPorBloque;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "puesto_id", nullable=false)
    private Puesto puesto;

    @OneToMany(mappedBy = "evaluacion", cascade= CascadeType.ALL, orphanRemoval= true)
    private List<Cuestionario> cuestionarios = new ArrayList<>();

    public void agregarCuestionario(Cuestionario cuestionario){
        cuestionarios.add(cuestionario);
        cuestionario.setEvaluacion(this);
    }

    public Evaluacion(String codigo, Puesto puesto, Integer duracionEnMinutos, Integer cantPreguntasPorBloque){
        this.codigo = codigo;
        this.puesto = puesto;
        this.duracionEnMinutos = duracionEnMinutos;
        this.cantPreguntasPorBloque = cantPreguntasPorBloque;
        this.fechaCreacion = LocalDateTime.now();
    }
}
