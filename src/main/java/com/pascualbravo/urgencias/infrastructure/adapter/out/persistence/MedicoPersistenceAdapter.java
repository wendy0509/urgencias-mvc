package com.pascualbravo.urgencias.infrastructure.adapter.out.persistence;

import com.pascualbravo.urgencias.domain.model.Medico;
import com.pascualbravo.urgencias.domain.port.out.MedicoRepositoryPort;
import com.pascualbravo.urgencias.infrastructure.adapter.out.persistence.mapper.PersistenceMapper;
import com.pascualbravo.urgencias.infrastructure.adapter.out.persistence.repository.SpringDataMedicoRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Adaptador de salida: implementa el puerto MedicoRepositoryPort con JPA + MySQL.
 * Si se cambia la tecnología de persistencia, solo se reemplaza esta clase.
 */
@Component
@Transactional
public class MedicoPersistenceAdapter implements MedicoRepositoryPort {

    private final SpringDataMedicoRepository jpa;

    public MedicoPersistenceAdapter(SpringDataMedicoRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Medico guardar(Medico medico) {
        return PersistenceMapper.aDominio(jpa.save(PersistenceMapper.aEntidad(medico)));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Medico> buscarPorId(Long id) {
        return jpa.findById(id).map(PersistenceMapper::aDominio);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Medico> listarTodos() {
        return jpa.findAll().stream().map(PersistenceMapper::aDominio).toList();
    }

    @Override
    public void eliminarPorId(Long id) {
        jpa.deleteById(id);
    }
}
