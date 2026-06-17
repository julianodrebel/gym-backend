package com.gymbackend.domain.usecases;

import com.gymbackend.domain.entities.VersaoFichaTreino;
import com.gymbackend.domain.exceptions.EntidadeNaoEncontradaException;
import com.gymbackend.domain.repositories.VersaoFichaTreinoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Caso de uso: Duplicar Versão de Ficha de Treino.
 * Cria nova versão com cópia dos exercícios da versão existente.
 */
@ApplicationScoped
public class DuplicarVersaoUseCase {

    private static final Logger LOG = Logger.getLogger(DuplicarVersaoUseCase.class);

    @Inject
    VersaoFichaTreinoRepository versaoRepository;

    @Inject
    CriarNovaVersaoUseCase criarNovaVersaoUseCase;

    @Transactional
    public VersaoFichaTreino executar(UUID fichaId, UUID versaoOrigemId) {
        LOG.infof("Duplicando versão: fichaId=%s, versaoOrigem=%s", fichaId, versaoOrigemId);

        VersaoFichaTreino versaoOrigem = versaoRepository.buscarPorId(versaoOrigemId)
                .orElseThrow(() -> EntidadeNaoEncontradaException.versaoFicha(versaoOrigemId));

        if (!versaoOrigem.getFichaTreinoId().equals(fichaId)) {
            throw new IllegalArgumentException("Versão não pertence à ficha informada.");
        }

        // Copiar exercícios da versão origem
        List<VersaoFichaTreino.ExercicioOrdem> exerciciosCopia = new ArrayList<>(versaoOrigem.getExercicios());

        VersaoFichaTreino novaCopia = criarNovaVersaoUseCase.executar(fichaId, exerciciosCopia);
        LOG.infof("Versão duplicada: novaVersaoId=%s", novaCopia.getId());
        return novaCopia;
    }
}
