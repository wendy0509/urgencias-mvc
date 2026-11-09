package com.pascualbravo.urgencias.controller;

import com.pascualbravo.urgencias.model.EstadoPaciente;
import com.pascualbravo.urgencias.model.Paciente;
import com.pascualbravo.urgencias.service.MedicoService;
import com.pascualbravo.urgencias.service.PacienteService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/pacientes")
public class PacienteController {

    private final PacienteService service;
    private final MedicoService medicoService;

    public PacienteController(PacienteService service, MedicoService medicoService) {
        this.service = service;
        this.medicoService = medicoService;
    }

    private void cargarCombos(Model model) {
        model.addAttribute("medicos", medicoService.listar());
        model.addAttribute("estados", EstadoPaciente.values());
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("pacientes", service.listar());
        return "pacientes/lista";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("paciente", new Paciente());
        cargarCombos(model);
        return "pacientes/formulario";
    }

    @GetMapping("/{id}")
    public String detalle(@PathVariable Long id, Model model) {
        model.addAttribute("paciente", service.buscarPorId(id));
        return "pacientes/detalle";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("paciente", service.buscarPorId(id));
        cargarCombos(model);
        return "pacientes/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute Paciente paciente, BindingResult result,
                          Model model, RedirectAttributes flash) {

        if (paciente.getMedico() == null || paciente.getMedico().getId() == null) {
            result.rejectValue("medico", "medico.requerido", "Debe seleccionar un médico");
        } else {
            paciente.setMedico(medicoService.buscarPorId(paciente.getMedico().getId()));
        }

        if (paciente.getDocumento() != null) {
            service.buscarPorDocumento(paciente.getDocumento()).ifPresent(existente -> {
                if (paciente.getId() == null || !existente.getId().equals(paciente.getId())) {
                    result.rejectValue("documento", "documento.duplicado",
                            "Ya existe un paciente con ese documento");
                }
            });
        }

        if (result.hasErrors()) {
            cargarCombos(model);
            return "pacientes/formulario";
        }
        service.guardar(paciente);
        flash.addFlashAttribute("mensaje", "Paciente guardado correctamente");
        return "redirect:/pacientes";
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id, RedirectAttributes flash) {
        service.eliminar(id);
        flash.addFlashAttribute("mensaje", "Paciente eliminado");
        return "redirect:/pacientes";
    }
}