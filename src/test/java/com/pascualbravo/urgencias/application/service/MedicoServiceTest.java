package com.pascualbravo.urgencias.application.service;

import com.pascualbravo.urgencias.domain.exception.RecursoNoEncontradoException;
import com.pascualbravo.urgencias.domain.exception.ReglaNegocioException;
import com.pascualbravo.urgencias.domain.model.Medico;
import com.pascualbravo.urgencias.domain.model.Paciente;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class MedicoServiceTest {

    private RepositoriosEnMemoria.Medicos medicos;
    private RepositoriosEnMemoria.Pacientes pacientes;
    private MedicoService servicio;

    @BeforeEach
    void preparar() {
        medicos = new RepositoriosEnMemoria.Medicos();
        pacientes = new RepositoriosEnMemoria.Pacientes();
        servicio = new MedicoService(medicos, pacientes);
    }

    private Medico nuevo() {
        return new Medico(null, "Carlos Ruiz", "Medicina general", "carlos@urgencias.co", true);
    }

    @Test
    void debeRegistrarYConsultar() {
        Medico m = servicio.registrar(nuevo());
        assertEquals("Carlos Ruiz", servicio.buscarPorId(m.getId()).getNombre());
        assertEquals(1, servicio.listar().size());
    }

    @Test
    void debeActualizar() {
        Medico m = servicio.registrar(nuevo());
        servicio.actualizar(m.getId(), new Medico(null, "Carlos Ruiz", "Cardiología", "c@urgencias.co", false));

        Medico actualizado = servicio.buscarPorId(m.getId());
        assertEquals("Cardiología", actualizado.getEspecialidad());
        assertFalse(actualizado.isDisponible());
    }

    @Test
    void noDebeEliminarMedicoConPacientes() {
        Medico m = servicio.registrar(nuevo());
        Paciente p = new Paciente();
        p.setDocumento("12345");
        p.setNivelGravedad(2);
        p.setFechaLlegada(LocalDateTime.now());
        p.asignarMedico(m);
        pacientes.guardar(p);

        assertThrows(ReglaNegocioException.class, () -> servicio.eliminar(m.getId()));
    }

    @Test
    void debeEliminarMedicoSinPacientes() {
        Medico m = servicio.registrar(nuevo());
        servicio.eliminar(m.getId());
        assertThrows(RecursoNoEncontradoException.class, () -> servicio.buscarPorId(m.getId()));
    }
}
