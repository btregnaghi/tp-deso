
package ar.edu.utn.frsf4.api.dto;

import ar.edu.utn.frsf4.api.model.enums.TipoDocumento;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CandidatoDTO {

    private Long id;
    private Integer nroCandidato;
    private String apellido;
    private String nombre;
    private TipoDocumento tipoDoc;
    private Integer nroDoc;

}
