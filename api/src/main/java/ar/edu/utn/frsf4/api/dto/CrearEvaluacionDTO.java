package ar.edu.utn.frsf4.api.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CrearEvaluacionDTO {
    private Long idPuesto;

    private List<Long> idsCandidatos;
}
