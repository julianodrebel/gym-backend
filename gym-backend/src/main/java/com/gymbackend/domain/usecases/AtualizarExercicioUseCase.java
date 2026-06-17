package com.gymbackend.domain.usecases;

import com.gymbackend.domain.entities.Exercicio;
import com.gymbackend.domain.exceptions.EntidadeNaoEncontradaException;
import com.gymbackend.domain.exceptions.RegraDeNegocioException;
import com.gymbackend.domain.repositories.ExercicioRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

import java.util.UUID;

/**
 * Caso de uso: Atualizar Exercício.
 * Atualiza nome e descrição sem afetar histórico.
 */
@ApplicationScoped
public class AtualizarExercicioUseCase {

    private static final Logger LOG = Logger.getLogger(AtualizarExercicioUseCase.class);

    @Inject
    ExercicioRepository exercicioRepository;

    @Transactional
    public Exercicio executar(UUID id, String novoNome, String novaDescricao) {
        LOG.infof("Atualizando exercício: id=%s", id);

        Exercicio exercicio = exercicioRepository.buscarPorId(id)
                .orElseThrow(() -> EntidadeNaoEncontradaException.exercicio(id));

        if (exercicioRepository.existePorNomeEIdDiferente(novoNome, id)) {
            throw new RegraDeNegocioException("Já existe um exercício com o nome: " + novoNome);
        }

        exercicio.atualizar(novoNome, novaDescricao);
        Exercicio atualizado = exercicioRepository.salvar(exercicio);

        LOG.infof("Exercício atualizado: id=%s", id);
        return atualizado;
    }
}
