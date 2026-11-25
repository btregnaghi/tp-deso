
package ar.edu.utn.frsf4.api.model;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import ar.edu.utn.frsf4.api.model.enums.TipoDocumento;
import ar.edu.utn.frsf4.api.model.enums.Genero;
import ar.edu.utn.frsf4.api.model.enums.NivelEscolaridad;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Candidato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Integer nroCandidato;

    @Column(nullable = false)
    private String apellido;

    @Column(nullable = false)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoDocumento tipoDoc;

    @Column(nullable = false)
    private Integer nroDoc;

    private LocalDate fechaNacimiento;

    @Enumerated(EnumType.STRING)
    private Genero genero;

    private String nacionalidad;

    private String email;

    @Enumerated(EnumType.STRING)
    private NivelEscolaridad escolaridad;

    @Column(nullable = false)
    private boolean eliminado = false;

    public Candidato(
            Integer nroCandidato,
            String apellido,
            String nombre,
            TipoDocumento tipoDoc,
            Integer nroDoc,
            LocalDate fechaNacimiento,
            Genero genero,
            String nacionalidad,
            String email,
            NivelEscolaridad escolaridad) {

        this.nroCandidato = nroCandidato;
        this.apellido = apellido;
        this.nombre = nombre;
        this.tipoDoc = tipoDoc;
        this.nroDoc = nroDoc;
        this.fechaNacimiento = fechaNacimiento;
        this.genero = genero;
        this.nacionalidad = nacionalidad;
        this.email = email;
        this.escolaridad = escolaridad;
        this.eliminado = false;
    }

    public boolean estaActivo() {
        return !this.eliminado;
    }
}
