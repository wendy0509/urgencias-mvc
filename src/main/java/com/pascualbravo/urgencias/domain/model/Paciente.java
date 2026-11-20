package com.pascualbravo.urgencias.domain.model;

import com.pascualbravo.urgencias.domain.exception.ReglaNegocioException;

import java.time.LocalDateTime;

/**
 * Paciente que llega a urgencias (modelo de dominio).
 * Contiene las reglas propias del paciente: rango de edad y gravedad,
 * médico obligatorio (relación muchos-a-uno) y clasificación de prioridad.
 */
public class Paciente {

    public static final int EDAD_MINIMA = 0;
    public static final int EDAD_MAXIMA = 120;
    public static final int GRAVEDAD_MINIMA = 1;
    public static final int GRAVEDAD_MAXIMA = 5;

    private Long id;
    private String nombre;
    private String documento;
    private Integer edad;
    private String motivoConsulta;
    private Integer nivelGravedad;
    private String alergias;
    private Prioridad prioridad;
    private LocalDateTime fechaLlegada;
    private EstadoPaciente estado = EstadoPaciente.EN_ESPERA;
    private Medico medico;

    /** Registra la hora de llegada al servicio (solo al crear el paciente). */
    public void registrarLlegada(LocalDateTime momento) {
        this.fechaLlegada = momento;
    }

    /** Asocia el paciente con el médico que lo atiende. La relación es obligatoria. */
    public void asignarMedico(Medico medico) {
        if (medico == null) {
            throw new ReglaNegocioException("El paciente debe tener un médico asignado");
        }
        this.medico = medico;
    }

    /** Valida los datos clínicos y calcula la prioridad de atención. */
    public void clasificar() {
        validarDatosClinicos();
        this.prioridad = Prioridad.calcular(nivelGravedad, edad);
    }

    private void validarDatosClinicos() {
        if (edad == null || edad < EDAD_MINIMA || edad > EDAD_MAXIMA) {
            throw new ReglaNegocioException("La edad debe estar entre " + EDAD_MINIMA + " y " + EDAD_MAXIMA);
        }
        if (nivelGravedad == null || nivelGravedad < GRAVEDAD_MINIMA || nivelGravedad > GRAVEDAD_MAXIMA) {
            throw new ReglaNegocioException("La gravedad debe estar entre " + GRAVEDAD_MINIMA + " y " + GRAVEDAD_MAXIMA);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDocumento() { return documento; }
    public void setDocumento(String documento) { this.documento = documento; }

    public Integer getEdad() { return edad; }
    public void setEdad(Integer edad) { this.edad = edad; }

    public String getMotivoConsulta() { return motivoConsulta; }
    public void setMotivoConsulta(String motivoConsulta) { this.motivoConsulta = motivoConsulta; }

    public Integer getNivelGravedad() { return nivelGravedad; }
    public void setNivelGravedad(Integer nivelGravedad) { this.nivelGravedad = nivelGravedad; }

    public String getAlergias() { return alergias; }
    public void setAlergias(String alergias) { this.alergias = alergias; }

    public Prioridad getPrioridad() { return prioridad; }
    public void setPrioridad(Prioridad prioridad) { this.prioridad = prioridad; }

    public LocalDateTime getFechaLlegada() { return fechaLlegada; }
    public void setFechaLlegada(LocalDateTime fechaLlegada) { this.fechaLlegada = fechaLlegada; }

    public EstadoPaciente getEstado() { return estado; }
    public void setEstado(EstadoPaciente estado) { this.estado = estado; }

    public Medico getMedico() { return medico; }
    public void setMedico(Medico medico) { this.medico = medico; }
}
