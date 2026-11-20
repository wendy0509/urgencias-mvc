package com.pascualbravo.urgencias.domain.exception;

/** Se lanza cuando una operación viola una regla del negocio de urgencias. */
public class ReglaNegocioException extends RuntimeException {
    public ReglaNegocioException(String mensaje) { super(mensaje); }
}
