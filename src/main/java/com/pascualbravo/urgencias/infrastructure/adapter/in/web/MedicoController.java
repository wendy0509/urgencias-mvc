package com.pascualbravo.urgencias.infrastructure.adapter.in.web;

import com.pascualbravo.urgencias.domain.exception.ReglaNegocioException;
import com.pascualbravo.urgencias.domain.port.in.GestionarMedicoUseCase;
import com.pascualbravo.urgencias.domain.port.in.GestionarPacienteUseCase;
import com.pascualbravo.urgencias.infrastructure.adapter.in.web.dto.MedicoForm;
import com.pascualbravo.urgencias.infrastructure.adapter.in.web.mapper.WebMapper;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Adaptador de entrada web para médicos. Depende solo de los puertos de entrada
 * (casos de uso); no conoce repositorios ni entidades JPA.
 */
@Controller
@RequestMapping("/medicos")
public class MedicoController {

    private final GestionarMedicoUseCase medicos;
    private final GestionarPacienteUseCase pacientes;

    public MedicoController(GestionarMedicoUseCase medicos, GestionarPacienteUseCase pacientes) {
        this.medicos = medicos;
        this.pacientes = pacientes;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("medicos", medicos.listar());
        return "medicos/lista";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("medico", new MedicoForm());
        return "medicos/formulario";
    }

    @GetMapping("/{id}")
    public String detalle(@PathVariable Long id, Model model) {
        model.addAttribute("medico", medicos.buscarPorId(id));
        model.addAttribute("pacientes", pacientes.listarPorMedico(id));
        return "medicos/detalle";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("medico", WebMapper.aFormulario(medicos.buscarPorId(id)));
        return "medicos/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("medico") MedicoForm form, BindingResult result,
                          RedirectAttributes flash) {
        if (result.hasErrors()) return "medicos/formulario";

        if (form.getId() == null) {
            medicos.registrar(WebMapper.aDominio(form));
        } else {
            medicos.actualizar(form.getId(), WebMapper.aDominio(form));
        }
        flash.addFlashAttribute("mensaje", "Médico guardado correctamente");
        return "redirect:/medicos";
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id, RedirectAttributes flash) {
        try {
            medicos.eliminar(id);
            flash.addFlashAttribute("mensaje", "Médico eliminado");
        } catch (ReglaNegocioException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/medicos";
    }
}
