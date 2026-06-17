package com.gymbackend.domain.exceptions;

/**
 * Exceção lançada ao tentar modificar uma versão imutável de ficha de treino.
 */
public class VersaoImutavelException extends RuntimeException {

    public VersaoImutavelException(String mensagem) {
        super(mensagem);
    }

    public VersaoImutavelException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
