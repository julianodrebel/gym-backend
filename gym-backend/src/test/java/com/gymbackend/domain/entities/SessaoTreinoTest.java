package com.gymbackend.domain.entities;

import com.gymbackend.TesteUnitarioBase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * Testes unitários para SessaoTreino.
 * Foco nas transições de status e audit trail.
 */
@DisplayName("SessaoTreino - Transições de Status")
class SessaoTreinoTest extends TesteUnitarioBase {

    private VersaoFichaTreino criarVersaoAtiva() {
        return new VersaoFichaTreino(ID_FICHA, 1);
    }

    @Test
    @DisplayName("Nova sessão deve ter status EM_PROGRESSO")
    void novaSessaoDeveEstarEmProgresso() {
        SessaoTreino sessao = new SessaoTreino(ID_USUARIO, criarVersaoAtiva());

        assertThat(sessao.getStatus()).isEqualTo(SessaoTreino.StatusSessao.EM_PROGRESSO);
        assertThat(sessao.estaEmProgresso()).isTrue();
        assertThat(sessao.getHoraInicio()).isNotNull();
        assertThat(sessao.getHoraFim()).isNull();
    }

    @Test
    @DisplayName("Deve registrar versão da ficha no audit trail")
    void deveRegistrarVersaoDaFichaAuditTrail() {
        VersaoFichaTreino versao = criarVersaoAtiva();
        SessaoTreino sessao = new SessaoTreino(ID_USUARIO, versao);

        assertThat(sessao.getVersaoFichaTreinoId()).isEqualTo(versao.getId());
    }

    @Test
    @DisplayName("Deve finalizar sessão em progresso")
    void deveFinalizarSessaoEmProgresso() {
        SessaoTreino sessao = new SessaoTreino(ID_USUARIO, criarVersaoAtiva());

        sessao.finalizar("Treino bom");

        assertThat(sessao.getStatus()).isEqualTo(SessaoTreino.StatusSessao.FINALIZADO);
        assertThat(sessao.getHoraFim()).isNotNull();
        assertThat(sessao.getObservacoesGerais()).isEqualTo("Treino bom");
    }

    @Test
    @DisplayName("Deve cancelar sessão em progresso")
    void deveCancelarSessaoEmProgresso() {
        SessaoTreino sessao = new SessaoTreino(ID_USUARIO, criarVersaoAtiva());

        sessao.cancelar();

        assertThat(sessao.getStatus()).isEqualTo(SessaoTreino.StatusSessao.CANCELADO);
        assertThat(sessao.getHoraFim()).isNotNull();
    }

    @Test
    @DisplayName("Deve rejeitar finalizar sessão já finalizada")
    void deveRejeitarFinalizarSessaoJaFinalizada() {
        SessaoTreino sessao = new SessaoTreino(ID_USUARIO, criarVersaoAtiva());
        sessao.finalizar(null);

        assertThatThrownBy(() -> sessao.finalizar(null))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("não está em progresso");
    }

    @Test
    @DisplayName("Deve rejeitar cancelar sessão já finalizada")
    void deveRejeitarCancelarSessaoJaFinalizada() {
        SessaoTreino sessao = new SessaoTreino(ID_USUARIO, criarVersaoAtiva());
        sessao.finalizar(null);

        assertThatThrownBy(sessao::cancelar)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("já finalizada");
    }

    @Test
    @DisplayName("Deve rejeitar criar sessão com versão deletada")
    void deveRejeitarSessaoComVersaoDeletada() {
        VersaoFichaTreino versao = criarVersaoAtiva();
        versao.setDeletadoEm(java.time.LocalDateTime.now());

        assertThatThrownBy(() -> new SessaoTreino(ID_USUARIO, versao))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("deletada");
    }
}
