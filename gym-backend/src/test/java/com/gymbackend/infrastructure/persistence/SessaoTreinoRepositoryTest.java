package com.gymbackend.infrastructure.persistence;

import com.gymbackend.TesteIntegracaoBase;
import com.gymbackend.domain.entities.FichaTreino;
import com.gymbackend.domain.entities.SessaoTreino;
import com.gymbackend.domain.entities.VersaoFichaTreino;
import com.gymbackend.domain.repositories.FichaTreinoRepository;
import com.gymbackend.domain.repositories.SessaoTreinoRepository;
import com.gymbackend.domain.repositories.VersaoFichaTreinoRepository;
import com.gymbackend.fixtures.ExercicioFixture;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

/**
 * Testes de integração para SessaoTreinoRepository.
 * Valida audit trail de versionamento e persistência.
 */
@DisplayName("SessaoTreinoRepository - Integração com H2")
class SessaoTreinoRepositoryTest extends TesteIntegracaoBase {

    @Inject
    FichaTreinoRepository fichaRepository;

    @Inject
    VersaoFichaTreinoRepository versaoRepository;

    @Inject
    SessaoTreinoRepository sessaoRepository;

    @Test
    @Transactional
    @DisplayName("Iniciar sessão com vínculo à versão da ficha (audit trail)")
    void iniciarSessaoComAuditTrail() {
        FichaTreino ficha = fichaRepository.salvar(ExercicioFixture.fichaValida());
        VersaoFichaTreino versao = versaoRepository.salvar(ExercicioFixture.versaoValida(ficha.getId()));

        SessaoTreino sessao = ExercicioFixture.sessaoEmProgresso(versao.getId());
        SessaoTreino salva = sessaoRepository.salvar(sessao);

        Optional<SessaoTreino> encontrada = sessaoRepository.buscarPorId(salva.getId());

        assertThat(encontrada).isPresent();
        assertThat(encontrada.get().getStatus())
                .isEqualTo(SessaoTreino.StatusSessao.EM_PROGRESSO);
        assertThat(encontrada.get().getVersaoFichaTreinoId())
                .isEqualTo(versao.getId());
    }

    @Test
    @Transactional
    @DisplayName("Listar sessões do usuário")
    void listarSessoesDoUsuario() {
        FichaTreino ficha = fichaRepository.salvar(ExercicioFixture.fichaValida());
        VersaoFichaTreino versao = versaoRepository.salvar(ExercicioFixture.versaoValida(ficha.getId()));

        sessaoRepository.salvar(ExercicioFixture.sessaoEmProgresso(versao.getId()));

        List<SessaoTreino> sessoes = sessaoRepository.listarPorUsuario(ID_USUARIO, 0, 10);

        assertThat(sessoes).isNotEmpty();
    }

    @Test
    @Transactional
    @DisplayName("Buscar ID inexistente retorna vazio")
    void buscarIdInexistenteRetornaVazio() {
        Optional<SessaoTreino> resultado = sessaoRepository.buscarPorId(java.util.UUID.randomUUID());
        assertThat(resultado).isEmpty();
    }
}
