package ar.edu.utn.frsf4.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ar.edu.utn.frsf4.api.model.Evaluacion;

@Repository 
public interface EvaluacionRepository extends JpaRepository<Evaluacion, Long>{

}
