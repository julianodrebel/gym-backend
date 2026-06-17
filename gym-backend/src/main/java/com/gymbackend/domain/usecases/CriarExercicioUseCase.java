package com.gymbackend.domain.usecases;

import com.gymbackend.domain.entities.Exercicio;
import com.gymbackend.domain.exceptions.RegraDeNegocioException;
import com.gymbackend.domain.repositories.ExercicioRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.jboss.logging.Logger;

/**
 * Caso de uso: Criar Exercício.
 * Orquestra validações de domínio e persistência.
 */
@ApplicationScoped
public class CriarExercicioUseCase {

    private static final Logger LOG = Logger.getLogger(CriarExercicioUseCase.class);

    @Inject
    ExercicioRepository exercicioRepository;

    @Transactional
    public Exercicio executar(String nome, String descricao, String grupoMuscular) {
        LOG.infof("Criando exercício: nome=%s, grupo=%s", nome, grupoMuscular);

        if (exercicioRepository.existePorNome(nome)) {
            throw new RegraDeNegocioException("Já existe um exercício ativo com o nome: " + nome);
        }

        Exercicio exercicio = new Exercicio(nome, descricao, grupoMuscular);
        Exercicio salvo = exercicioRepository.salvar(exercicio);

        LOG.infof("Exercício criado com sucesso: id=%s", salvo.getId());
        return salvo;
    }
}
