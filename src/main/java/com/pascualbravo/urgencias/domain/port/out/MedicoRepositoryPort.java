package com.pascualbravo.urgencias.domain.port.out;

import com.pascualbravo.urgencias.domain.model.Medico;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida: lo que el dominio necesita para guardar y consultar médicos.
 * La implementación (JPA, JDBC, memoria, API externa…) vive en infraestructura.
 */
public interface MedicoRepositoryPort {

    Medico guardar(Medico medico);

    Optional<Medico> buscarPorId(Long id);

    List<Medico> listarTodos();

    void eliminarPorId(Long id);
}
