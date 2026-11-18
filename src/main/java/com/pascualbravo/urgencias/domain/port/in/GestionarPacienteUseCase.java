package com.pascualbravo.urgencias.domain.port.in;

import com.pascualbravo.urgencias.domain.model.Paciente;

import java.util.List;

/**
 * Puerto de entrada: casos de uso que el sistema ofrece sobre los pacientes.
 */
public interface GestionarPacienteUseCase {

    /** Registra un paciente nuevo asignado al médico indicado y calcula su prioridad. */
    Paciente registrar(Paciente datos, Long medicoId);

    /** Lista los pacientes ordenados por gravedad (mayor primero) y hora de llegada. */
    List<Paciente> listarPorPrioridad();

    Paciente buscarPorId(Long id);

    /** Pacientes asignados a un médico (lado "uno a muchos" de la relación). */
    List<Paciente> listarPorMedico(Long medicoId);

    /** Actualiza los datos, conserva la hora de llegada y recalcula la prioridad. */
    Paciente actualizar(Long id, Paciente datos, Long medicoId);

    void eliminar(Long id);
}
