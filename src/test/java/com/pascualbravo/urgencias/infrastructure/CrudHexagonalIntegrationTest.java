package com.pascualbravo.urgencias.infrastructure;

import com.pascualbravo.urgencias.domain.model.Medico;
import com.pascualbravo.urgencias.domain.model.Paciente;
import com.pascualbravo.urgencias.domain.model.Prioridad;
import com.pascualbravo.urgencias.domain.port.in.GestionarMedicoUseCase;
import com.pascualbravo.urgencias.domain.port.in.GestionarPacienteUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Prueba de punta a punta: navegador simulado (MockMvc) → controladores → casos de uso →
 * adaptadores JPA → base de datos H2. Demuestra que el CRUD de ambas entidades y su
 * relación siguen funcionando después de la migración a hexagonal.
 */
@SpringBootTest
class CrudHexagonalIntegrationTest {

    @Autowired private WebApplicationContext contexto;
    @Autowired private GestionarMedicoUseCase medicos;
    @Autowired private GestionarPacienteUseCase pacientes;

    private MockMvc mvc;

    @BeforeEach
    void preparar() {
        mvc = MockMvcBuilders.webAppContextSetup(contexto).build();
    }

    @Test
    void crudCompletoDeMedicosYPacientesConSuRelacion() throws Exception {
        // ── Médico: registrar ──
        mvc.perform(post("/medicos/guardar")
                        .param("nombre", "Laura Gómez")
                        .param("especialidad", "Pediatría")
                        .param("correo", "laura@urgencias.co")
                        .param("disponible", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/medicos"));

        Medico medico = medicos.listar().stream()
                .filter(m -> m.getCorreo().equals("laura@urgencias.co")).findFirst().orElseThrow();

        // ── Médico: listar, consultar, actualizar ──
        mvc.perform(get("/medicos")).andExpect(status().isOk()).andExpect(view().name("medicos/lista"));
        mvc.perform(get("/medicos/" + medico.getId())).andExpect(status().isOk());
        mvc.perform(get("/medicos/" + medico.getId() + "/editar")).andExpect(status().isOk());
        mvc.perform(post("/medicos/guardar")
                        .param("id", medico.getId().toString())
                        .param("nombre", "Laura Gómez")
                        .param("especialidad", "Urgencias pediátricas")
                        .param("correo", "laura@urgencias.co"))
                .andExpect(redirectedUrl("/medicos"));
        assertEquals("Urgencias pediátricas", medicos.buscarPorId(medico.getId()).getEspecialidad());

        // ── Médico: validación de formulario ──
        mvc.perform(post("/medicos/guardar").param("nombre", "").param("especialidad", "X").param("correo", "malo"))
                .andExpect(status().isOk())
                .andExpect(view().name("medicos/formulario"))
                .andExpect(model().attributeHasFieldErrors("medico", "nombre", "correo"));

        // ── Paciente: registrar asociado al médico ──
        mvc.perform(post("/pacientes/guardar")
                        .param("nombre", "Ana Pérez")
                        .param("documento", "1020304050")
                        .param("edad", "3")
                        .param("motivoConsulta", "Fiebre alta")
                        .param("nivelGravedad", "4")
                        .param("estado", "EN_ESPERA")
                        .param("medicoId", medico.getId().toString()))
                .andExpect(redirectedUrl("/pacientes"));

        Paciente paciente = pacientes.listarPorMedico(medico.getId()).get(0);
        assertEquals(Prioridad.CRITICA, paciente.getPrioridad());
        assertNotNull(paciente.getFechaLlegada());
        assertEquals(medico.getId(), paciente.getMedico().getId());

        // ── Paciente: listar, consultar, documento duplicado, actualizar ──
        mvc.perform(get("/pacientes")).andExpect(status().isOk()).andExpect(view().name("pacientes/lista"));
        mvc.perform(get("/pacientes/" + paciente.getId())).andExpect(status().isOk());
        mvc.perform(get("/pacientes/" + paciente.getId() + "/editar")).andExpect(status().isOk());

        mvc.perform(post("/pacientes/guardar")
                        .param("nombre", "Otro Paciente").param("documento", "1020304050")
                        .param("edad", "40").param("motivoConsulta", "Tos")
                        .param("nivelGravedad", "2").param("medicoId", medico.getId().toString()))
                .andExpect(view().name("pacientes/formulario"))
                .andExpect(model().attributeHasFieldErrors("paciente", "documento"));

        mvc.perform(post("/pacientes/guardar")
                        .param("id", paciente.getId().toString())
                        .param("nombre", "Ana Pérez")
                        .param("documento", "1020304050")
                        .param("edad", "30")
                        .param("motivoConsulta", "Fiebre controlada")
                        .param("nivelGravedad", "1")
                        .param("estado", "EN_ATENCION")
                        .param("medicoId", medico.getId().toString()))
                .andExpect(redirectedUrl("/pacientes"));
        Paciente actualizado = pacientes.buscarPorId(paciente.getId());
        assertEquals(Prioridad.BAJA, actualizado.getPrioridad());
        assertEquals(paciente.getFechaLlegada(), actualizado.getFechaLlegada());

        // ── Relación: no se elimina un médico con pacientes ──
        mvc.perform(post("/medicos/" + medico.getId() + "/eliminar"))
                .andExpect(flash().attributeExists("error"));
        assertNotNull(medicos.buscarPorId(medico.getId()));

        // ── Eliminar paciente y luego médico ──
        mvc.perform(post("/pacientes/" + paciente.getId() + "/eliminar")).andExpect(redirectedUrl("/pacientes"));
        assertTrue(pacientes.listarPorMedico(medico.getId()).isEmpty());

        mvc.perform(post("/medicos/" + medico.getId() + "/eliminar"))
                .andExpect(flash().attribute("mensaje", "Médico eliminado"));

        // ── Registro inexistente → página 404 amigable ──
        mvc.perform(get("/medicos/" + medico.getId()))
                .andExpect(status().isNotFound())
                .andExpect(view().name("error/no-encontrado"));
    }
}
