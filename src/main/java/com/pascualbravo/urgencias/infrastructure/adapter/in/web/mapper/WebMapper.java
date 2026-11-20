package com.pascualbravo.urgencias.infrastructure.adapter.in.web.mapper;

import com.pascualbravo.urgencias.domain.model.Medico;
import com.pascualbravo.urgencias.domain.model.Paciente;
import com.pascualbravo.urgencias.infrastructure.adapter.in.web.dto.MedicoForm;
import com.pascualbravo.urgencias.infrastructure.adapter.in.web.dto.PacienteForm;

/** Traduce entre los formularios web y el modelo de dominio. */
public final class WebMapper {

    private WebMapper() { }

    public static Medico aDominio(MedicoForm f) {
        return new Medico(f.getId(), f.getNombre(), f.getEspecialidad(), f.getCorreo(), f.isDisponible());
    }

    public static MedicoForm aFormulario(Medico m) {
        MedicoForm f = new MedicoForm();
        f.setId(m.getId());
        f.setNombre(m.getNombre());
        f.setEspecialidad(m.getEspecialidad());
        f.setCorreo(m.getCorreo());
        f.setDisponible(m.isDisponible());
        return f;
    }

    public static Paciente aDominio(PacienteForm f) {
        Paciente p = new Paciente();
        p.setId(f.getId());
        p.setNombre(f.getNombre());
        p.setDocumento(f.getDocumento());
        p.setEdad(f.getEdad());
        p.setMotivoConsulta(f.getMotivoConsulta());
        p.setNivelGravedad(f.getNivelGravedad());
        p.setAlergias(f.getAlergias());
        p.setEstado(f.getEstado());
        return p;
    }

    public static PacienteForm aFormulario(Paciente p) {
        PacienteForm f = new PacienteForm();
        f.setId(p.getId());
        f.setNombre(p.getNombre());
        f.setDocumento(p.getDocumento());
        f.setEdad(p.getEdad());
        f.setMotivoConsulta(p.getMotivoConsulta());
        f.setNivelGravedad(p.getNivelGravedad());
        f.setAlergias(p.getAlergias());
        f.setEstado(p.getEstado());
        f.setMedicoId(p.getMedico() == null ? null : p.getMedico().getId());
        return f;
    }
}
