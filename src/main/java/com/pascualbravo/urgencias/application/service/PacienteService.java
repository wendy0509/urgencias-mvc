package com.pascualbravo.urgencias.application.service;

import com.pascualbravo.urgencias.domain.exception.DocumentoDuplicadoException;
import com.pascualbravo.urgencias.domain.exception.RecursoNoEncontradoException;
import com.pascualbravo.urgencias.domain.model.Medico;
import com.pascualbravo.urgencias.domain.model.Paciente;
import com.pascualbravo.urgencias.domain.port.in.GestionarPacienteUseCase;
import com.pascualbravo.urgencias.domain.port.out.MedicoRepositoryPort;
import com.pascualbravo.urgencias.domain.port.out.PacienteRepositoryPort;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio de aplicación: implementa los casos de uso de pacientes.
 * Valida el documento único, resuelve la relación con el médico,
 * registra la llegada y delega la clasificación al dominio.
 */
public class PacienteService implements GestionarPacienteUseCase {

    private final PacienteRepositoryPort pacientes;
    private final MedicoRepositoryPort medicos;
    private final Clock reloj;

    public PacienteService(PacienteRepositoryPort pacientes, MedicoRepositoryPort medicos, Clock reloj) {
        this.pacientes = pacientes;
        this.medicos = medicos;
        this.reloj = reloj;
    }

    @Override
    public Paciente registrar(Paciente datos, Long medicoId) {
        validarDocumentoUnico(datos.getDocumento(), null);
        datos.setId(null);
        datos.asignarMedico(buscarMedico(medicoId));
        datos.registrarLlegada(LocalDateTime.now(reloj));
        datos.clasificar();
        return pacientes.guardar(datos);
    }

    @Override
    public List<Paciente> listarPorPrioridad() {
        return pacientes.listarOrdenadosPorGravedadYLlegada();
    }

    @Override
    public Paciente buscarPorId(Long id) {
        return pacientes.buscarPorId(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Paciente", id));
    }

    @Override
    public List<Paciente> listarPorMedico(Long medicoId) {
        buscarMedico(medicoId);
        return pacientes.listarPorMedico(medicoId);
    }

    @Override
    public Paciente actualizar(Long id, Paciente datos, Long medicoId) {
        Paciente existente = buscarPorId(id);
        validarDocumentoUnico(datos.getDocumento(), id);
        datos.setId(id);
        datos.registrarLlegada(existente.getFechaLlegada()); // la llegada no cambia al editar
        datos.asignarMedico(buscarMedico(medicoId));
        datos.clasificar();
        return pacientes.guardar(datos);
    }

    @Override
    public void eliminar(Long id) {
        buscarPorId(id);
        pacientes.eliminarPorId(id);
    }

    private Medico buscarMedico(Long medicoId) {
        if (medicoId == null) {
            throw new RecursoNoEncontradoException("Médico", null);
        }
        return medicos.buscarPorId(medicoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Médico", medicoId));
    }

    private void validarDocumentoUnico(String documento, Long idActual) {
        if (documento == null) return;
        pacientes.buscarPorDocumento(documento).ifPresent(otro -> {
            if (idActual == null || !otro.getId().equals(idActual)) {
                throw new DocumentoDuplicadoException(documento);
            }
        });
    }
}
