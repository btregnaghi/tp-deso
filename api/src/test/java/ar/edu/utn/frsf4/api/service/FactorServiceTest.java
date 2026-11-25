package ar.edu.utn.frsf4.api.service;

import ar.edu.utn.frsf4.api.dto.FactorDTO;
import ar.edu.utn.frsf4.api.model.Competencia;
import ar.edu.utn.frsf4.api.model.Factor;
import ar.edu.utn.frsf4.api.repository.FactorRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FactorServiceTest {

    @Mock
    private FactorRepository factorRepository;

    @InjectMocks
    private FactorService factorService;

    @Test
    void debeObtenerFactoresActivos() {

        Competencia competencia = new Competencia();
        competencia.setId(1L);
        competencia.setNombre("Desarrollo");

        Factor factor = new Factor();
        factor.setId(2L);
        factor.setNombre("Calidad de código");
        factor.setCompetencia(competencia);

        when(factorRepository.findByFechaBajaIsNull())
                .thenReturn(List.of(factor));

        List<FactorDTO> resultado =
                factorService.obtenerFactoresActivos();

        assertEquals(1, resultado.size());
        assertEquals(2L, resultado.get(0).getId());
        assertEquals("Calidad de código",
                resultado.get(0).getNombre());
        assertEquals(1L, resultado.get(0).getCompetenciaId());

        verify(factorRepository).findByFechaBajaIsNull();
    }

    @Test
    void debeRetornarListaVaciaSiNoHayFactores() {

        when(factorRepository.findByFechaBajaIsNull())
                .thenReturn(List.of());

        List<FactorDTO> resultado =
                factorService.obtenerFactoresActivos();

        assertTrue(resultado.isEmpty());

        verify(factorRepository).findByFechaBajaIsNull();
    }
}