package ar.edu.utn.frsf4.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter 
@Setter 
@NoArgsConstructor 
@AllArgsConstructor 
public class ClaveEvaluacionCandidatoDTO {

    private Integer nroCandidato;

    private String nombre;

    private String apellido;

    private String claveAcceso;
}
