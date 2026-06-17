package com.gymbackend.domain.usecases;

import com.gymbackend.domain.entities.FichaTreino;
import com.gymbackend.domain.entities.VersaoFichaTreino;
import com.gymbackend.domain.exceptions.EntidadeNaoEncontradaException;
import com.gymbackend.domain.repositories.FichaTreinoRepository;
import com.gymbackend.domain.repositories.VersaoFichaTreinoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

import java.util.List;
import java.util.UUID;

/**
 * Caso de uso: Criar Nova Versão de Ficha de Treino.
 * Garante imutabilidade da versão anterior.
 * Cria nova versão com exercícios fornecidos.
 */
@ApplicationScoped
public class CriarNovaVersaoUseCase {

    private static final Logger LOG = Logger.getLogger(CriarNovaVersaoUseCase.class);

    @Inject
    FichaTreinoRepository fichaTreinoRepository;

    @Inject
    VersaoFichaTreinoRepository versaoRepository;

    @Transactional
    public VersaoFichaTreino executar(UUID fichaId, List<VersaoFichaTreino.ExercicioOrdem> exercicios) {
        LOG.infof("Criando nova versão para ficha: id=%s", fichaId);

        FichaTreino ficha = fichaTreinoRepository.buscarPorId(fichaId)
                .orElseThrow(() -> EntidadeNaoEncontradaException.fichaTreino(fichaId));

        int proximoNumero = versaoRepository.contarVersoesPorFicha(fichaId) + 1;

        VersaoFichaTreino novaVersao = new VersaoFichaTreino(fichaId, proximoNumero);
        if (exercicios != null) {
            novaVersao.setExercicios(exercicios);
        }

        VersaoFichaTreino salva = versaoRepository.salvar(novaVersao);

        // Ativar nova versão
        ficha.ativarVersao(salva);
        fichaTreinoRepository.salvar(ficha);

        LOG.infof("Nova versão V%d criada: id=%s", proximoNumero, salva.getId());
        return salva;
    }
}
