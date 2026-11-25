package ar.edu.utn.frsf4.api.service;

import ar.edu.utn.frsf4.api.dto.FactorDTO;
import ar.edu.utn.frsf4.api.repository.FactorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FactorService {

    private final FactorRepository factorRepository;

    public FactorService(FactorRepository factorRepository) {
        this.factorRepository = factorRepository;
    }

    @Transactional(readOnly = true)
    public List<FactorDTO> obtenerFactoresActivos() {

        return factorRepository.findByFechaBajaIsNull()
                .stream()
                .map(factor -> new FactorDTO(
                        factor.getId(),
                        factor.getNombre(),
                        factor.getCompetencia().getId()
                ))
                .toList();
    }
}