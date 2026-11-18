package com.pascualbravo.urgencias.application.service;

import com.pascualbravo.urgencias.domain.model.Medico;
import com.pascualbravo.urgencias.domain.model.Paciente;
import com.pascualbravo.urgencias.domain.port.out.MedicoRepositoryPort;
import com.pascualbravo.urgencias.domain.port.out.PacienteRepositoryPort;

import java.util.*;

/**
 * Adaptadores de salida falsos, en memoria. Demuestran que los servicios de aplicación
 * funcionan con cualquier implementación de los puertos, sin MySQL ni Spring.
 */
final class RepositoriosEnMemoria {

    private RepositoriosEnMemoria() { }

    static class Medicos implements MedicoRepositoryPort {
        private final Map<Long, Medico> datos = new LinkedHashMap<>();
        private long secuencia = 1;

        @Override
        public Medico guardar(Medico m) {
            if (m.getId() == null) m.setId(secuencia++);
            datos.put(m.getId(), m);
            return m;
        }

        @Override
        public Optional<Medico> buscarPorId(Long id) { return Optional.ofNullable(datos.get(id)); }

        @Override
        public List<Medico> listarTodos() { return new ArrayList<>(datos.values()); }

        @Override
        public void eliminarPorId(Long id) { datos.remove(id); }
    }

    static class Pacientes implements PacienteRepositoryPort {
        private final Map<Long, Paciente> datos = new LinkedHashMap<>();
        private long secuencia = 1;

        @Override
        public Paciente guardar(Paciente p) {
            if (p.getId() == null) p.setId(secuencia++);
            datos.put(p.getId(), p);
            return p;
        }

        @Override
        public Optional<Paciente> buscarPorId(Long id) { return Optional.ofNullable(datos.get(id)); }

        @Override
        public Optional<Paciente> buscarPorDocumento(String documento) {
            return datos.values().stream().filter(p -> p.getDocumento().equals(documento)).findFirst();
        }

        @Override
        public List<Paciente> listarOrdenadosPorGravedadYLlegada() {
            return datos.values().stream()
                    .sorted(Comparator.comparing(Paciente::getNivelGravedad).reversed()
                            .thenComparing(Paciente::getFechaLlegada))
                    .toList();
        }

        @Override
        public List<Paciente> listarPorMedico(Long medicoId) {
            return datos.values().stream().filter(p -> p.getMedico().getId().equals(medicoId)).toList();
        }

        @Override
        public boolean existenPacientesDelMedico(Long medicoId) { return !listarPorMedico(medicoId).isEmpty(); }

        @Override
        public void eliminarPorId(Long id) { datos.remove(id); }
    }
}
