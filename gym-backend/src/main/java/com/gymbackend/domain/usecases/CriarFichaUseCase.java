package com.gymbackend.domain.usecases;

import com.gymbackend.domain.entities.FichaTreino;
import com.gymbackend.domain.entities.VersaoFichaTreino;
import com.gymbackend.domain.repositories.FichaTreinoRepository;
import com.gymbackend.domain.repositories.VersaoFichaTreinoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

import java.util.UUID;

/**
 * Caso de uso: Criar Ficha de Treino.
 * Cria ficha com V1 automaticamente.
 */
@ApplicationScoped
public class CriarFichaUseCase {

    private static final Logger LOG = Logger.getLogger(CriarFichaUseCase.class);

    @Inject
    FichaTreinoRepository fichaTreinoRepository;

    @Inject
    VersaoFichaTreinoRepository versaoRepository;

    @Transactional
    public FichaTreino executar(UUID usuarioId, String nome) {
        LOG.infof("Criando ficha de treino: usuarioId=%s, nome=%s", usuarioId, nome);

        FichaTreino ficha = new FichaTreino(usuarioId, nome);
        FichaTreino salva = fichaTreinoRepository.salvar(ficha);

        // Criar V1 automaticamente
        VersaoFichaTreino v1 = new VersaoFichaTreino(salva.getId(), 1);
        VersaoFichaTreino versaoSalva = versaoRepository.salvar(v1);

        // Ativar V1
        salva.ativarVersao(versaoSalva);
        salva = fichaTreinoRepository.salvar(salva);

        LOG.infof("Ficha criada: id=%s com V1=%s", salva.getId(), versaoSalva.getId());
        return salva;
    }
}
