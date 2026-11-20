package com.pascualbravo.urgencias.domain.exception;

/** Ya existe otro paciente registrado con el mismo documento. */
public class DocumentoDuplicadoException extends ReglaNegocioException {
    public DocumentoDuplicadoException(String documento) {
        super("Ya existe un paciente con el documento " + documento);
    }
}
