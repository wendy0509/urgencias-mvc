package com.pascualbravo.urgencias.controller;

import com.pascualbravo.urgencias.model.Medico;
import com.pascualbravo.urgencias.service.MedicoService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/medicos")
public class MedicoController {

    private final MedicoService service;

    public MedicoController(MedicoService service) { this.service = service; }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("medicos", service.listar());
        return "medicos/lista";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("medico", new Medico());
        return "medicos/formulario";
    }

    @GetMapping("/{id}")
    public String detalle(@PathVariable Long id, Model model) {
        model.addAttribute("medico", service.buscarPorId(id));
        return "medicos/detalle";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("medico", service.buscarPorId(id));
        return "medicos/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute Medico medico, BindingResult result,
                          RedirectAttributes flash) {
        if (result.hasErrors()) return "medicos/formulario";
        service.guardar(medico);
        flash.addFlashAttribute("mensaje", "Médico guardado correctamente");
        return "redirect:/medicos";
    }

    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Long id, RedirectAttributes flash) {
        try {
            service.eliminar(id);
            flash.addFlashAttribute("mensaje", "Médico eliminado");
        } catch (IllegalStateException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/medicos";
    }
}