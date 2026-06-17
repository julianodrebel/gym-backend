package com.gymbackend.domain.exceptions;

/**
 * Exceção lançada quando uma regra de negócio é violada.
 */
public class RegraDeNegocioException extends RuntimeException {

    public RegraDeNegocioException(String mensagem) {
        super(mensagem);
    }
}
