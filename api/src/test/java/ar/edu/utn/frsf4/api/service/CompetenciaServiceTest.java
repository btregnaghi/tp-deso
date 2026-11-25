package ar.edu.utn.frsf4.api.service;

import ar.edu.utn.frsf4.api.dto.CompetenciaDTO;
import ar.edu.utn.frsf4.api.model.Competencia;
import ar.edu.utn.frsf4.api.repository.CompetenciaRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompetenciaServiceTest {

    @Mock
    private CompetenciaRepository competenciaRepository;

    @InjectMocks
    private CompetenciaService competenciaService;

    @Test
    void debeObtenerCompetenciasActivas() {

        Competencia competencia = new Competencia();
        competencia.setId(1L);
        competencia.setNombre("Desarrollo");

        when(competenciaRepository.findByFechaBajaIsNull())
                .thenReturn(List.of(competencia));

        List<CompetenciaDTO> resultado =
                competenciaService.obtenerCompetenciasActivas();

        assertEquals(1, resultado.size());
        assertEquals(1L, resultado.get(0).getId());
        assertEquals("Desarrollo", resultado.get(0).getNombre());

        verify(competenciaRepository).findByFechaBajaIsNull();
    }

    @Test
    void debeRetornarListaVaciaSiNoHayCompetencias() {

        when(competenciaRepository.findByFechaBajaIsNull())
                .thenReturn(List.of());

        List<CompetenciaDTO> resultado =
                competenciaService.obtenerCompetenciasActivas();

        assertTrue(resultado.isEmpty());

        verify(competenciaRepository).findByFechaBajaIsNull();
    }
}