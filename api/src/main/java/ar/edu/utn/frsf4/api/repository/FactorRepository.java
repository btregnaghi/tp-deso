package ar.edu.utn.frsf4.api.repository;

import ar.edu.utn.frsf4.api.model.Factor;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FactorRepository extends JpaRepository<Factor, Long> {

    List<Factor> findByFechaBajaIsNull();

}