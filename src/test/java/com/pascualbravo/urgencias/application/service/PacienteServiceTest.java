package com.pascualbravo.urgencias.application.service;

import com.pascualbravo.urgencias.domain.exception.DocumentoDuplicadoException;
import com.pascualbravo.urgencias.domain.exception.RecursoNoEncontradoException;
import com.pascualbravo.urgencias.domain.model.EstadoPaciente;
import com.pascualbravo.urgencias.domain.model.Medico;
import com.pascualbravo.urgencias.domain.model.Paciente;
import com.pascualbravo.urgencias.domain.model.Prioridad;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.*;

class PacienteServiceTest {

    private static final ZoneId BOGOTA = ZoneId.of("America/Bogota");

    private RepositoriosEnMemoria.Medicos medicos;
    private RepositoriosEnMemoria.Pacientes pacientes;
    private PacienteService servicio;
    private Medico medico;

    @BeforeEach
    void preparar() {
        medicos = new RepositoriosEnMemoria.Medicos();
        pacientes = new RepositoriosEnMemoria.Pacientes();
        Clock reloj = Clock.fixed(Instant.parse("2026-11-18T15:00:00Z"), BOGOTA);
        servicio = new PacienteService(pacientes, medicos, reloj);
        medico = medicos.guardar(new Medico(null, "Laura Gómez", "Pediatría", "laura@urgencias.co", true));
    }

    private Paciente datos(String documento, int edad, int gravedad) {
        Paciente p = new Paciente();
        p.setNombre("Ana Pérez");
        p.setDocumento(documento);
        p.setEdad(edad);
        p.setNivelGravedad(gravedad);
        p.setMotivoConsulta("Fiebre alta");
        return p;
    }

    @Test
    void debeRegistrarConPrioridadLlegadaYMedico() {
        Paciente p = servicio.registrar(datos("12345", 3, 4), medico.getId());

        assertNotNull(p.getId());
        assertEquals(Prioridad.CRITICA, p.getPrioridad());
        assertEquals(LocalDateTime.of(2026, 11, 18, 10, 0), p.getFechaLlegada());
        assertEquals(medico.getId(), p.getMedico().getId());
    }

    @Test
    void debeRechazarDocumentoDuplicado() {
        servicio.registrar(datos("12345", 30, 2), medico.getId());
        assertThrows(DocumentoDuplicadoException.class,
                () -> servicio.registrar(datos("12345", 40, 3), medico.getId()));
    }

    @Test
    void debeRechazarMedicoInexistente() {
        assertThrows(RecursoNoEncontradoException.class,
                () -> servicio.registrar(datos("12345", 30, 2), 999L));
    }

    @Test
    void debeActualizarConservandoLlegadaYRecalculandoPrioridad() {
        Paciente original = servicio.registrar(datos("12345", 30, 5), medico.getId());
        Paciente cambios = datos("12345", 30, 1);
        cambios.setEstado(EstadoPaciente.EN_ATENCION);

        Paciente actualizado = servicio.actualizar(original.getId(), cambios, medico.getId());

        assertEquals(Prioridad.BAJA, actualizado.getPrioridad());
        assertEquals(original.getFechaLlegada(), actualizado.getFechaLlegada());
        assertEquals(EstadoPaciente.EN_ATENCION, actualizado.getEstado());
    }

    @Test
    void debeListarPorGravedadYPorMedico() {
        servicio.registrar(datos("11111", 30, 2), medico.getId());
        servicio.registrar(datos("22222", 30, 5), medico.getId());

        assertEquals("22222", servicio.listarPorPrioridad().get(0).getDocumento());
        assertEquals(2, servicio.listarPorMedico(medico.getId()).size());
    }

    @Test
    void debeEliminar() {
        Paciente p = servicio.registrar(datos("12345", 30, 2), medico.getId());
        servicio.eliminar(p.getId());
        assertTrue(servicio.listarPorPrioridad().isEmpty());
    }
}
