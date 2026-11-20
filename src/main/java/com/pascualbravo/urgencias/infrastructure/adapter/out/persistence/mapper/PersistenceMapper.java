package com.pascualbravo.urgencias.infrastructure.adapter.out.persistence.mapper;

import com.pascualbravo.urgencias.domain.model.Medico;
import com.pascualbravo.urgencias.domain.model.Paciente;
import com.pascualbravo.urgencias.domain.model.Prioridad;
import com.pascualbravo.urgencias.infrastructure.adapter.out.persistence.entity.MedicoEntity;
import com.pascualbravo.urgencias.infrastructure.adapter.out.persistence.entity.PacienteEntity;

/** Traduce entre el modelo de dominio y las entidades JPA. */
public final class PersistenceMapper {

    private PersistenceMapper() { }

    public static Medico aDominio(MedicoEntity e) {
        if (e == null) return null;
        return new Medico(e.getId(), e.getNombre(), e.getEspecialidad(), e.getCorreo(), e.isDisponible());
    }

    public static MedicoEntity aEntidad(Medico m) {
        MedicoEntity e = new MedicoEntity();
        e.setId(m.getId());
        e.setNombre(m.getNombre());
        e.setEspecialidad(m.getEspecialidad());
        e.setCorreo(m.getCorreo());
        e.setDisponible(m.isDisponible());
        return e;
    }

    public static Paciente aDominio(PacienteEntity e) {
        if (e == null) return null;
        Paciente p = new Paciente();
        p.setId(e.getId());
        p.setNombre(e.getNombre());
        p.setDocumento(e.getDocumento());
        p.setEdad(e.getEdad());
        p.setMotivoConsulta(e.getMotivoConsulta());
        p.setNivelGravedad(e.getNivelGravedad());
        p.setAlergias(e.getAlergias());
        p.setPrioridad(e.getPrioridad() == null ? null : Prioridad.valueOf(e.getPrioridad()));
        p.setFechaLlegada(e.getFechaLlegada());
        p.setEstado(e.getEstado());
        p.setMedico(aDominio(e.getMedico()));
        return p;
    }

    /** El médico se pasa ya resuelto como entidad gestionada por JPA. */
    public static PacienteEntity aEntidad(Paciente p, MedicoEntity medico) {
        PacienteEntity e = new PacienteEntity();
        e.setId(p.getId());
        e.setNombre(p.getNombre());
        e.setDocumento(p.getDocumento());
        e.setEdad(p.getEdad());
        e.setMotivoConsulta(p.getMotivoConsulta());
        e.setNivelGravedad(p.getNivelGravedad());
        e.setAlergias(p.getAlergias());
        e.setPrioridad(p.getPrioridad() == null ? null : p.getPrioridad().name());
        e.setFechaLlegada(p.getFechaLlegada());
        e.setEstado(p.getEstado());
        e.setMedico(medico);
        return e;
    }
}
