package com.pascualbravo.urgencias.service;

import com.pascualbravo.urgencias.model.Paciente;
import com.pascualbravo.urgencias.repository.PacienteRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PacienteService {

    private final PacienteRepository repo;

    public PacienteService(PacienteRepository repo) { this.repo = repo; }

    public List<Paciente> listar() {
        return repo.findAllByOrderByNivelGravedadDescFechaLlegadaAsc();
    }

    public Paciente buscarPorId(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Paciente no encontrado: " + id));
    }

    public Optional<Paciente> buscarPorDocumento(String documento) {
        return repo.findByDocumento(documento);
    }

    public void guardar(Paciente p) {
        if (p.getId() == null) {
            p.setFechaLlegada(LocalDateTime.now());
        } else {
            p.setFechaLlegada(buscarPorId(p.getId()).getFechaLlegada());
        }
        p.setPrioridad(calcularPrioridad(p));
        repo.save(p);
    }

    public void eliminar(Long id) { repo.deleteById(id); }

    // Versión simplificada del Motor de Clasificación (RF02)
    private String calcularPrioridad(Paciente p) {
        int puntos = p.getNivelGravedad();
        if (p.getEdad() <= 5 || p.getEdad() >= 70) puntos++;
        if (puntos >= 5) return "CRITICA";
        if (puntos == 4) return "ALTA";
        if (puntos == 3) return "MEDIA";
        return "BAJA";
    }
}