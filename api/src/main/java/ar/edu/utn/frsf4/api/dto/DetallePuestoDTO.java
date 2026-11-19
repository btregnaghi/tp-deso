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
public class DetallePuestoDTO {

    private String codigo;

    private String nombre;

    private String empresa;

    private List<CompetenciaPonderadaDTO> competencias;

}