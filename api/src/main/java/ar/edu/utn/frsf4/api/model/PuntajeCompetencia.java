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
public class PuntajeCompetencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable= false)
    private Double puntaje;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "competencia_id", nullable = false)
    private Competencia competencia;

    @OneToMany(mappedBy = "puntajeCompetencia", cascade= CascadeType.ALL, orphanRemoval= true)
    private List<PuntajeFactor> puntajesFactor = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cuestionario_id", nullable = false)
    private Cuestionario cuestionario;

    public PuntajeCompetencia(Competencia competencia){
        this.competencia = competencia;
    }

    public void agregarPuntajeFactor(PuntajeFactor puntajeFactor){
        this.puntajesFactor.add(puntajeFactor);
        puntajeFactor.setPuntajeCompetencia(this);
    }
}
