package com.pascualbravo.urgencias.infrastructure.adapter.in.web;

import com.pascualbravo.urgencias.domain.exception.DocumentoDuplicadoException;
import com.pascualbravo.urgencias.domain.exception.ReglaNegocioException;
import com.pascualbravo.urgencias.domain.model.EstadoPaciente;
import com.pascualbravo.urgencias.domain.model.Paciente;
import com.pascualbravo.urgencias.domain.port.in.GestionarMedicoUseCase;
import com.pascualbravo.urgencias.domain.port.in.GestionarPacienteUseCase;
import com.pascualbravo.urgencias.infrastructure.adapter.in.web.dto.PacienteForm;
import com.pascualbravo.urgencias.infrastructure.adapter.in.web.mapper.WebMapper;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Adaptador de entrada web para pacientes. Traduce HTTP ⇄ casos de uso:
 * valida el formato del formulario, invoca el puerto de entrada y elige la vista.
 * Las reglas de negocio (documento único, prioridad, médico obligatorio) están en
 * el servicio de aplicación y en el dominio, no aquí.
 */
@Controller
@RequestMapping("/pacientes")
public class PacienteController {

    private final GestionarPacienteUseCase pacientes;
    private final GestionarMedicoUseCase medicos;

    public PacienteController(GestionarPacienteUseCase pacientes, GestionarMedicoUseCase medicos) {
        this.pacientes = pacientes;
        this.medicos = medicos;
    }

    private void cargarCombos(Model model) {
        model.addAttribute("medicos", medicos.listar());
        model.addAttribute("estados", EstadoPaciente.values());
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("pacientes", pacientes.listarPorPrioridad());
        return "pacientes/lista";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("paciente", new PacienteForm());
        cargarCombos(model);
        return "pacientes/formulario";
    }

    @GetMapping("/{id}")
    public String detalle(@PathVariable Long id, Model model) {
        model.addAttribute("paciente", pacientes.buscarPorId(id));
        return "pacientes/detalle";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("paciente", WebMapper.aFormulario(pacientes.buscarPorId(id)));
        cargarCombos(model);
        return "pacientes/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("paciente") PacienteForm form, BindingResult result,
                          Model model, RedirectAttributes flash) {
        if (!result.hasErrors()) {
            try {
                Paciente datos = WebMapper.aDominio(form);
                if (form.getId() == null) {
                    pacientes.registrar(datos, form.getMedicoId());
                } else {
                    pacientes.actualizar(form.getId(), datos, form.getMedicoId());
                }
                flash.addFlashAttribute("mensaje", "Paciente guardado correctamente");
                return "redirect:/pacientes";
            } catch (DocumentoDuplicadoException e) {
                result.rejectValue("documento", "documento.duplicado", "Ya existe un paciente con ese documento");
            } catch (ReglaNegocioException e) {
                result.reject("regla.negocio", e.getMessage());
            }
        }
        cargarCombos(model);
        return "pacientes/formulario";
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id, RedirectAttributes flash) {
        pacientes.eliminar(id);
        flash.addFlashAttribute("mensaje", "Paciente eliminado");
        return "redirect:/pacientes";
    }
}
