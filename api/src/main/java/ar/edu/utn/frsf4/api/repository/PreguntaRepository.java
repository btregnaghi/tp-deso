
package ar.edu.utn.frsf4.api.repository;

import ar.edu.utn.frsf4.api.model.Pregunta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PreguntaRepository extends JpaRepository<Pregunta, Long> {

    List<Pregunta> findByFechaBajaIsNull();

}
