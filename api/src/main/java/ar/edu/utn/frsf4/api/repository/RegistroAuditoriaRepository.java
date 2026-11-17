package ar.edu.utn.frsf4.api.repository;

import ar.edu.utn.frsf4.api.model.RegistroAuditoria;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegistroAuditoriaRepository
        extends JpaRepository<RegistroAuditoria, Long> {

}