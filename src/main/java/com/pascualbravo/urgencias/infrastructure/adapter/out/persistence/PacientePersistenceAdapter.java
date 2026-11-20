package com.pascualbravo.urgencias.infrastructure.adapter.out.persistence;

import com.pascualbravo.urgencias.domain.model.Paciente;
import com.pascualbravo.urgencias.domain.port.out.PacienteRepositoryPort;
import com.pascualbravo.urgencias.infrastructure.adapter.out.persistence.entity.MedicoEntity;
import com.pascualbravo.urgencias.infrastructure.adapter.out.persistence.mapper.PersistenceMapper;
import com.pascualbravo.urgencias.infrastructure.adapter.out.persistence.repository.SpringDataMedicoRepository;
import com.pascualbravo.urgencias.infrastructure.adapter.out.persistence.repository.SpringDataPacienteRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Adaptador de salida: implementa el puerto PacienteRepositoryPort con JPA + MySQL.
 * Resuelve la llave foránea medico_id a partir del médico del dominio.
 */
@Component
@Transactional
public class PacientePersistenceAdapter implements PacienteRepositoryPort {

    private final SpringDataPacienteRepository jpa;
    private final SpringDataMedicoRepository medicosJpa;

    public PacientePersistenceAdapter(SpringDataPacienteRepository jpa, SpringDataMedicoRepository medicosJpa) {
        this.jpa = jpa;
        this.medicosJpa = medicosJpa;
    }

    @Override
    public Paciente guardar(Paciente paciente) {
        MedicoEntity medico = medicosJpa.getReferenceById(paciente.getMedico().getId());
        return PersistenceMapper.aDominio(jpa.save(PersistenceMapper.aEntidad(paciente, medico)));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Paciente> buscarPorId(Long id) {
        return jpa.findById(id).map(PersistenceMapper::aDominio);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Paciente> buscarPorDocumento(String documento) {
        return jpa.findByDocumento(documento).map(PersistenceMapper::aDominio);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Paciente> listarOrdenadosPorGravedadYLlegada() {
        return jpa.findAllByOrderByNivelGravedadDescFechaLlegadaAsc().stream()
                .map(PersistenceMapper::aDominio).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Paciente> listarPorMedico(Long medicoId) {
        return jpa.findByMedicoIdOrderByNivelGravedadDescFechaLlegadaAsc(medicoId).stream()
                .map(PersistenceMapper::aDominio).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existenPacientesDelMedico(Long medicoId) {
        return jpa.existsByMedicoId(medicoId);
    }

    @Override
    public void eliminarPorId(Long id) {
        jpa.deleteById(id);
    }
}
