package ar.edu.utn.frsf4.api.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ValidarCandidatosResponseDTO {

    private boolean puedeAvanzar;

    private List<CandidatoDTO> candidatosBloqueados;

}