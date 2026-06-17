package com.gymbackend.domain.usecases;

import com.gymbackend.domain.entities.Exercicio;
import com.gymbackend.domain.exceptions.EntidadeNaoEncontradaException;
import com.gymbackend.domain.repositories.ExercicioRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

import java.util.UUID;

/**
 * Caso de uso: Inativar Exercício (soft delete).
 * Preserva o histórico de execuções.
 */
@ApplicationScoped
public class InativarExercicioUseCase {

    private static final Logger LOG = Logger.getLogger(InativarExercicioUseCase.class);

    @Inject
    ExercicioRepository exercicioRepository;

    @Transactional
    public Exercicio executar(UUID id) {
        LOG.infof("Inativando exercício: id=%s", id);

        Exercicio exercicio = exercicioRepository.buscarPorId(id)
                .orElseThrow(() -> EntidadeNaoEncontradaException.exercicio(id));

        exercicio.inativar();
        Exercicio atualizado = exercicioRepository.salvar(exercicio);

        LOG.infof("Exercício inativado: id=%s", id);
        return atualizado;
    }
}
