package com.gymbackend.domain.exceptions;

/**
 * Exceção lançada quando uma entidade não é encontrada.
 */
public class EntidadeNaoEncontradaException extends RuntimeException {

    public EntidadeNaoEncontradaException(String mensagem) {
        super(mensagem);
    }

    public static EntidadeNaoEncontradaException exercicio(java.util.UUID id) {
        return new EntidadeNaoEncontradaException("Exercício não encontrado: " + id);
    }

    public static EntidadeNaoEncontradaException fichaTreino(java.util.UUID id) {
        return new EntidadeNaoEncontradaException("Ficha de treino não encontrada: " + id);
    }

    public static EntidadeNaoEncontradaException versaoFicha(java.util.UUID id) {
        return new EntidadeNaoEncontradaException("Versão da ficha não encontrada: " + id);
    }

    public static EntidadeNaoEncontradaException sessaoTreino(java.util.UUID id) {
        return new EntidadeNaoEncontradaException("Sessão de treino não encontrada: " + id);
    }
}
