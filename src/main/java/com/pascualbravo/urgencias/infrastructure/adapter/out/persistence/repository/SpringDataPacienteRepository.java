package com.pascualbravo.urgencias.infrastructure.adapter.out.persistence.repository;

import com.pascualbravo.urgencias.infrastructure.adapter.out.persistence.entity.PacienteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/** Repositorio Spring Data: detalle técnico interno del adaptador de persistencia. */
public interface SpringDataPacienteRepository extends JpaRepository<PacienteEntity, Long> {

    List<PacienteEntity> findAllByOrderByNivelGravedadDescFechaLlegadaAsc();

    List<PacienteEntity> findByMedicoIdOrderByNivelGravedadDescFechaLlegadaAsc(Long medicoId);

    Optional<PacienteEntity> findByDocumento(String documento);

    boolean existsByMedicoId(Long medicoId);
}
