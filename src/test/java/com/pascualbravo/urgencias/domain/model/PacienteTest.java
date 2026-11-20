package com.pascualbravo.urgencias.domain.model;

import com.pascualbravo.urgencias.domain.exception.ReglaNegocioException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PacienteTest {

    private Paciente paciente(int edad, int gravedad) {
        Paciente p = new Paciente();
        p.setEdad(edad);
        p.setNivelGravedad(gravedad);
        return p;
    }

    @Test
    void debeCalcularPrioridadAlClasificar() {
        Paciente p = paciente(80, 4);
        p.clasificar();
        assertEquals(Prioridad.CRITICA, p.getPrioridad());
    }

    @Test
    void debeRechazarGravedadFueraDeRango() {
        assertThrows(ReglaNegocioException.class, () -> paciente(30, 9).clasificar());
    }

    @Test
    void debeRechazarEdadFueraDeRango() {
        assertThrows(ReglaNegocioException.class, () -> paciente(130, 2).clasificar());
    }

    @Test
    void debeExigirMedicoAsignado() {
        assertThrows(ReglaNegocioException.class, () -> new Paciente().asignarMedico(null));
    }
}
