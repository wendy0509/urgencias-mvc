package com.pascualbravo.urgencias.application.service;

import com.pascualbravo.urgencias.domain.exception.RecursoNoEncontradoException;
import com.pascualbravo.urgencias.domain.exception.ReglaNegocioException;
import com.pascualbravo.urgencias.domain.model.Medico;
import com.pascualbravo.urgencias.domain.port.in.GestionarMedicoUseCase;
import com.pascualbravo.urgencias.domain.port.out.MedicoRepositoryPort;
import com.pascualbravo.urgencias.domain.port.out.PacienteRepositoryPort;

import java.util.List;

/**
 * Servicio de aplicación: implementa los casos de uso de médicos
 * orquestando el dominio y los puertos de salida.
 * No tiene anotaciones de Spring; se registra como bean en infrastructure/config.
 */
public class MedicoService implements GestionarMedicoUseCase {

    private final MedicoRepositoryPort medicos;
    private final PacienteRepositoryPort pacientes;

    public MedicoService(MedicoRepositoryPort medicos, PacienteRepositoryPort pacientes) {
        this.medicos = medicos;
        this.pacientes = pacientes;
    }

    @Override
    public Medico registrar(Medico medico) {
        medico.setId(null);
        return medicos.guardar(medico);
    }

    @Override
    public List<Medico> listar() {
        return medicos.listarTodos();
    }

    @Override
    public Medico buscarPorId(Long id) {
        return medicos.buscarPorId(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Médico", id));
    }

    @Override
    public Medico actualizar(Long id, Medico datos) {
        Medico existente = buscarPorId(id);
        existente.actualizarDatos(datos);
        return medicos.guardar(existente);
    }

    @Override
    public void eliminar(Long id) {
        buscarPorId(id);
        if (pacientes.existenPacientesDelMedico(id)) {
            throw new ReglaNegocioException("No se puede eliminar: el médico tiene pacientes asignados.");
        }
        medicos.eliminarPorId(id);
    }
}
