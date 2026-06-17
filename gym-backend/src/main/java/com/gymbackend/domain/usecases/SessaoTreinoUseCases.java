package com.gymbackend.domain.usecases;

import com.gymbackend.domain.entities.ExercicioExecutado;
import com.gymbackend.domain.entities.SerieExecutada;
import com.gymbackend.domain.entities.SessaoTreino;
import com.gymbackend.domain.entities.VersaoFichaTreino;
import com.gymbackend.domain.exceptions.EntidadeNaoEncontradaException;
import com.gymbackend.domain.repositories.ExercicioExecutadoRepository;
import com.gymbackend.domain.repositories.FichaTreinoRepository;
import com.gymbackend.domain.repositories.SessaoTreinoRepository;
import com.gymbackend.domain.repositories.VersaoFichaTreinoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Casos de uso para gerenciamento de sessões de treino.
 */
@ApplicationScoped
public class SessaoTreinoUseCases {

    private static final Logger LOG = Logger.getLogger(SessaoTreinoUseCases.class);

    @Inject
    SessaoTreinoRepository sessaoRepository;

    @Inject
    FichaTreinoRepository fichaRepository;

    @Inject
    VersaoFichaTreinoRepository versaoRepository;

    @Inject
    ExercicioExecutadoRepository exercicioExecRepository;

    /**
     * Inicia nova sessão de treino a partir da ficha ativa.
     * Registra a versão utilizada (audit trail).
     */
    @Transactional
    public SessaoTreino iniciarSessao(UUID usuarioId, UUID fichaId) {
        LOG.infof("Iniciando sessão: usuarioId=%s, fichaId=%s", usuarioId, fichaId);

        var ficha = fichaRepository.buscarPorId(fichaId)
                .orElseThrow(() -> EntidadeNaoEncontradaException.fichaTreino(fichaId));

        if (ficha.getVersaoAtivaId() == null) {
            throw new IllegalStateException("Ficha não possui versão ativa.");
        }

        VersaoFichaTreino versaoAtiva = versaoRepository.buscarPorId(ficha.getVersaoAtivaId())
                .orElseThrow(() -> EntidadeNaoEncontradaException.versaoFicha(ficha.getVersaoAtivaId()));

        SessaoTreino sessao = new SessaoTreino(usuarioId, versaoAtiva);

        // Criar ExercicioExecutado para cada exercício na versão
        int ordem = 0;
        for (VersaoFichaTreino.ExercicioOrdem exercicioOrdem : versaoAtiva.getExercicios()) {
            ExercicioExecutado exec = new ExercicioExecutado(sessao.getId(), exercicioOrdem.getExercicioId(), ordem++);
            sessao.getExercicios().add(exec);
        }

        SessaoTreino salva = sessaoRepository.salvar(sessao);
        LOG.infof("Sessão iniciada: id=%s, versão=%s", salva.getId(), versaoAtiva.getId());
        return salva;
    }

    /**
     * Registra uma série executada durante a sessão.
     * Valida peso >= 0 e reps >= 0.
     */
    @Transactional
    public SerieExecutada registrarSerie(UUID sessaoId, UUID exercicioExecutadoId,
                                         BigDecimal peso, Integer repeticoes) {
        LOG.infof("Registrando série: sessaoId=%s, execId=%s, peso=%s, reps=%d",
                sessaoId, exercicioExecutadoId, peso, repeticoes);

        SessaoTreino sessao = sessaoRepository.buscarPorId(sessaoId)
                .orElseThrow(() -> EntidadeNaoEncontradaException.sessaoTreino(sessaoId));

        if (!sessao.estaEmProgresso()) {
            throw new IllegalStateException("Sessão não está em progresso.");
        }

        ExercicioExecutado exec = exercicioExecRepository.buscarPorId(exercicioExecutadoId)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Exercício executado não encontrado: " + exercicioExecutadoId));

        int proximoNumero = exec.proximoNumeroSerie();
        SerieExecutada serie = new SerieExecutada(exercicioExecutadoId, proximoNumero, peso, repeticoes);

        SerieExecutada salva = exercicioExecRepository.salvarSerie(serie);
        LOG.infof("Série registrada: id=%s, serie=%d", salva.getId(), proximoNumero);
        return salva;
    }

    /**
     * Finaliza a sessão de treino.
     */
    @Transactional
    public SessaoTreino finalizarSessao(UUID sessaoId, String observacoes) {
        LOG.infof("Finalizando sessão: id=%s", sessaoId);

        SessaoTreino sessao = sessaoRepository.buscarPorId(sessaoId)
                .orElseThrow(() -> EntidadeNaoEncontradaException.sessaoTreino(sessaoId));

        sessao.finalizar(observacoes);
        SessaoTreino salva = sessaoRepository.salvar(sessao);

        LOG.infof("Sessão finalizada: id=%s", sessaoId);
        return salva;
    }

    /**
     * Cancela a sessão de treino.
     */
    @Transactional
    public SessaoTreino cancelarSessao(UUID sessaoId) {
        LOG.infof("Cancelando sessão: id=%s", sessaoId);

        SessaoTreino sessao = sessaoRepository.buscarPorId(sessaoId)
                .orElseThrow(() -> EntidadeNaoEncontradaException.sessaoTreino(sessaoId));

        sessao.cancelar();
        SessaoTreino salva = sessaoRepository.salvar(sessao);

        LOG.infof("Sessão cancelada: id=%s", sessaoId);
        return salva;
    }
}
