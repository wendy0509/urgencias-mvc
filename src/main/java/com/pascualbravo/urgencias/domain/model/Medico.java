package com.pascualbravo.urgencias.domain.model;

/**
 * Médico del servicio de urgencias (modelo de dominio).
 * Es un objeto Java puro: no tiene anotaciones de JPA ni de validación web.
 */
public class Medico {

    private Long id;
    private String nombre;
    private String especialidad;
    private String correo;
    private boolean disponible = true;

    public Medico() { }

    public Medico(Long id, String nombre, String especialidad, String correo, boolean disponible) {
        this.id = id;
        this.nombre = nombre;
        this.especialidad = especialidad;
        this.correo = correo;
        this.disponible = disponible;
    }

    /** Copia los datos editables de otro médico, conservando la identidad de este. */
    public void actualizarDatos(Medico datos) {
        this.nombre = datos.getNombre();
        this.especialidad = datos.getEspecialidad();
        this.correo = datos.getCorreo();
        this.disponible = datos.isDisponible();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getEspecialidad() { return especialidad; }
    public void setEspecialidad(String especialidad) { this.especialidad = especialidad; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public boolean isDisponible() { return disponible; }
    public void setDisponible(boolean disponible) { this.disponible = disponible; }
}
