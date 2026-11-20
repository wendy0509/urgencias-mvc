package com.pascualbravo.urgencias.domain.exception;

/** El registro solicitado (paciente o médico) no existe. */
public class RecursoNoEncontradoException extends RuntimeException {
    public RecursoNoEncontradoException(String recurso, Long id) {
        super(recurso + " no encontrado: " + id);
    }
}
