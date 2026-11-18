package com.pascualbravo.urgencias.domain.port.in;

import com.pascualbravo.urgencias.domain.model.Medico;

import java.util.List;

/**
 * Puerto de entrada: casos de uso que el sistema ofrece sobre los médicos.
 * Los adaptadores de entrada (p. ej. controladores web) solo conocen esta interfaz.
 */
public interface GestionarMedicoUseCase {

    Medico registrar(Medico medico);

    List<Medico> listar();

    Medico buscarPorId(Long id);

    Medico actualizar(Long id, Medico datos);

    /** Falla con ReglaNegocioException si el médico tiene pacientes asignados. */
    void eliminar(Long id);
}
