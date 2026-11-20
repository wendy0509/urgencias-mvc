package com.pascualbravo.urgencias.domain.port.out;

import com.pascualbravo.urgencias.domain.model.Paciente;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida: lo que el dominio necesita para guardar y consultar pacientes.
 */
public interface PacienteRepositoryPort {

    Paciente guardar(Paciente paciente);

    Optional<Paciente> buscarPorId(Long id);

    Optional<Paciente> buscarPorDocumento(String documento);

    /** Gravedad descendente y, a igual gravedad, el que llegó primero. */
    List<Paciente> listarOrdenadosPorGravedadYLlegada();

    List<Paciente> listarPorMedico(Long medicoId);

    boolean existenPacientesDelMedico(Long medicoId);

    void eliminarPorId(Long id);
}
