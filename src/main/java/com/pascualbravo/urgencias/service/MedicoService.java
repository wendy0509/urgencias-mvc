package com.pascualbravo.urgencias.service;

import com.pascualbravo.urgencias.model.Medico;
import com.pascualbravo.urgencias.repository.MedicoRepository;
import com.pascualbravo.urgencias.repository.PacienteRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class MedicoService {

    private final MedicoRepository repo;
    private final PacienteRepository pacienteRepo;

    public MedicoService(MedicoRepository repo, PacienteRepository pacienteRepo) {
        this.repo = repo;
        this.pacienteRepo = pacienteRepo;
    }

    public List<Medico> listar() { return repo.findAll(); }

    public Medico buscarPorId(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Médico no encontrado: " + id));
    }

    public void guardar(Medico m) { repo.save(m); }

    public void eliminar(Long id) {
        if (pacienteRepo.existsByMedicoId(id)) {
            throw new IllegalStateException("No se puede eliminar: el médico tiene pacientes asignados.");
        }
        repo.deleteById(id);
    }
}