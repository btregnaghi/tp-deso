
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
}
