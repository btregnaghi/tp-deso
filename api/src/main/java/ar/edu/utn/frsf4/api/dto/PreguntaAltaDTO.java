package ar.edu.utn.frsf4.api.dto;

import ar.edu.utn.frsf4.api.model.enums.TipoPregunta;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PreguntaAltaDTO {

    private Long competenciaId;
    private Long factorId;
    private String nombre;
    private String texto;
    private String descripcion;
    private TipoPregunta tipo;
    private List<OpcionDTO> opciones;

}