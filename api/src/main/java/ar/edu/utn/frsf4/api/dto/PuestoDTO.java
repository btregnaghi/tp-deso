package ar.edu.utn.frsf4.api.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PuestoDTO {

    private Long id;

    private String codigo;

    private String nombre;

    private String empresa;

}