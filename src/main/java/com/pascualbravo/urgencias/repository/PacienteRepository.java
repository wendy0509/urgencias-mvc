package com.pascualbravo.urgencias.repository;

import com.pascualbravo.urgencias.model.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface PacienteRepository extends JpaRepository<Paciente, Long> {
    List<Paciente> findAllByOrderByNivelGravedadDescFechaLlegadaAsc();
    Optional<Paciente> findByDocumento(String documento);
    boolean existsByMedicoId(Long medicoId);
}