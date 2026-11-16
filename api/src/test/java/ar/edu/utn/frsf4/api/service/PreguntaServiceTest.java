
package ar.edu.utn.frsf4.api.service;

import ar.edu.utn.frsf4.api.model.Pregunta;
import ar.edu.utn.frsf4.api.repository.PreguntaRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verifyNoInteractions;

import ar.edu.utn.frsf4.api.dto.PreguntaDTO;
import static org.mockito.Mockito.verify;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ar.edu.utn.frsf4.api.model.Competencia;
import ar.edu.utn.frsf4.api.model.Factor;
import ar.edu.utn.frsf4.api.model.Opcion;
import ar.edu.utn.frsf4.api.model.enums.TipoPregunta;

@ExtendWith(MockitoExtension.class)
class PreguntaServiceTest {

    @Mock
    private PreguntaRepository preguntaRepository;

    @InjectMocks
    private PreguntaService preguntaService;

    @Test
    void obtenerPreguntasActivas_debeRetornarLasPreguntasDelRepositorio() {

        // 1. Preparar los datos de prueba
        Pregunta pregunta1 = new Pregunta();
        Pregunta pregunta2 = new Pregunta();

        List<Pregunta> preguntas = List.of(pregunta1, pregunta2);

        // 2. Simular la respuesta del Repository
        when(preguntaRepository.findByFechaBajaIsNull())
                .thenReturn(preguntas);

        // 3. Ejecutar el metodo del Service
        List<Pregunta> resultado =
                preguntaService.obtenerPreguntasActivas();

        // 4. Verificar el resultado
        assertEquals(2, resultado.size());
    }

    @Test
void buscarPreguntas_sinCriterios_debeLanzarExcepcion() {

    IllegalArgumentException excepcion = assertThrows(
        IllegalArgumentException.class,
        () -> preguntaService.buscarPreguntas(null, null, null)
    );

    assertEquals(
        "Debe ingresar al menos un criterio de búsqueda.",
        excepcion.getMessage()
    );

    verifyNoInteractions(preguntaRepository);
}


@Test
void buscarPreguntas_conEspacios_debeLanzarExcepcion() {

    IllegalArgumentException excepcion = assertThrows(
        IllegalArgumentException.class,
        () -> preguntaService.buscarPreguntas("   ", "", "  ")
    );

    assertEquals(
        "Debe ingresar al menos un criterio de búsqueda.",
        excepcion.getMessage()
    );

    verifyNoInteractions(preguntaRepository);
}

@Test
void buscarPreguntas_conCriteriosValidos_debeInvocarRepositorio() {

    when(preguntaRepository.buscarPorCriterios(
        "Tecnica", "Programacion", "Java"
    )).thenReturn(List.of());

    preguntaService.buscarPreguntas(
        "Tecnica", "Programacion", "Java"
    );

    verify(preguntaRepository).buscarPorCriterios(
        "Tecnica", "Programacion", "Java"
    );
}
@Test
void buscarPreguntas_debeNormalizarCriterios() {

    when(preguntaRepository.buscarPorCriterios(
        null, "Programacion", null
    )).thenReturn(List.of());

    preguntaService.buscarPreguntas(
        "   ", "  Programacion  ", ""
    );

    verify(preguntaRepository).buscarPorCriterios(
        null, "Programacion", null
    );
}

@Test
void buscarPreguntas_sinCoincidencias_debeRetornarListaVacia() {

    when(preguntaRepository.buscarPorCriterios(
        null, null, "Java"
    )).thenReturn(List.of());

    List<PreguntaDTO> resultado =
        preguntaService.buscarPreguntas(null, null, "Java");

    assertTrue(resultado.isEmpty());
}

@Test
void buscarPreguntas_conCoincidencias_debeRetornarDTO() {

    // 1. Crear competencia
    Competencia competencia = new Competencia();
    competencia.setId(1L);
    competencia.setNombre("Tecnica");

    // 2. Crear factor
    Factor factor = new Factor();
    factor.setId(2L);
    factor.setNombre("Programacion");
    factor.setCompetencia(competencia);

    // 3. Crear pregunta
    Pregunta pregunta = new Pregunta(
        "Java",
        "¿Que es la herencia?",
        "Conceptos de POO",
        TipoPregunta.OPCION_SIMPLE,
        factor
    );

    pregunta.setId(3L);

    // 4. Crear opcion
    Opcion opcion = new Opcion();
    opcion.setOrdenVisualizacion(1);
    opcion.setTexto("Reutilizacion de codigo");
    opcion.setPonderacion(10);

    pregunta.agregarOpcion(opcion);

    // 5. Simular el comportamiento del Repository
    when(preguntaRepository.buscarPorCriterios(
        null, null, "Java"
    )).thenReturn(List.of(pregunta));

    // 6. Ejecutar el metodo que queremos probar
    List<PreguntaDTO> resultado =
        preguntaService.buscarPreguntas(null, null, "Java");

    // 7. Verificar el resultado
    assertEquals(1, resultado.size());

    PreguntaDTO dto = resultado.get(0);

    assertEquals(pregunta.getId(), dto.getId());
    assertEquals(pregunta.getVersion(), dto.getVersion());
    assertEquals(pregunta.getNombre(), dto.getNombre());
    assertEquals(pregunta.getTexto(), dto.getTexto());
    assertEquals(pregunta.getDescripcion(), dto.getDescripcion());
    assertEquals(pregunta.getTipo(), dto.getTipo());
    assertEquals(pregunta.getFechaModificacion(), dto.getFechaModificacion());
    assertEquals(pregunta.getFechaBaja(), dto.getFechaBaja());

    assertEquals(1L, dto.getCompetenciaId());
    assertEquals("Tecnica", dto.getCompetenciaNombre());

    assertEquals(2L, dto.getFactorId());
    assertEquals("Programacion", dto.getFactorNombre());

    assertEquals(1, dto.getOpciones().size());
    assertEquals(1, dto.getOpciones().get(0).getOrdenVisualizacion());
    assertEquals("Reutilizacion de codigo",
                 dto.getOpciones().get(0).getTexto());
    assertEquals(10, dto.getOpciones().get(0).getPonderacion());
}

}

