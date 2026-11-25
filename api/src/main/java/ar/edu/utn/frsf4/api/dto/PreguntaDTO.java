package ar.edu.utn.frsf4.api.dto;

import ar.edu.utn.frsf4.api.model.enums.TipoPregunta;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PreguntaDTO {

    private Long id;
    private Integer version;
    private String nombre;
    private String texto;
    private String descripcion;
    private TipoPregunta tipo;
    private LocalDateTime fechaModificacion;
    private LocalDate fechaBaja;

    private Long competenciaId;
    private String competenciaNombre;

    private Long factorId;
    private String factorNombre;

    private List<OpcionDTO> opciones;

}