package com.gymbackend.domain.entities;

import com.gymbackend.TesteUnitarioBase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

/**
 * Testes unitários para a entidade VersaoFichaTreino.
 * Foco na validação de imutabilidade - requisito CRÍTICO.
 */
@DisplayName("VersaoFichaTreino - Imutabilidade (Requisito Crítico)")
class VersaoFichaTreinoTest extends TesteUnitarioBase {

    @Test
    @DisplayName("Deve criar versão com dados válidos")
    void deveCriarVersaoComDadosValidos() {
        VersaoFichaTreino versao = new VersaoFichaTreino(ID_FICHA, 1);

        assertThat(versao.getId()).isNotNull();
        assertThat(versao.getNumero()).isEqualTo(1);
        assertThat(versao.getFichaTreinoId()).isEqualTo(ID_FICHA);
        assertThat(versao.getCriadoEm()).isNotNull();
        assertThat(versao.getDeletadoEm()).isNull();
    }

    @Test
    @DisplayName("validarNaoModificavel DEVE sempre lançar exceção (imutabilidade)")
    void validarNaoModificavelDeveAlwaysLancarExcecao() {
        VersaoFichaTreino versao = new VersaoFichaTreino(ID_FICHA, 1);

        assertThatThrownBy(versao::validarNaoModificavel)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("imutável");
    }

    @Test
    @DisplayName("Deve rejeitar fichaId nulo")
    void deveRejeitarFichaIdNulo() {
        assertThatThrownBy(() -> new VersaoFichaTreino(null, 1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ID da ficha");
    }

    @Test
    @DisplayName("Deve rejeitar número de versão menor que 1")
    void deveRejeitarNumeroMenorQueUm() {
        assertThatThrownBy(() -> new VersaoFichaTreino(ID_FICHA, 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Número da versão");
    }

    @Test
    @DisplayName("Deve detectar versão ativa (sem deletadoEm)")
    void deveDetectarVersaoAtiva() {
        VersaoFichaTreino versao = new VersaoFichaTreino(ID_FICHA, 1);

        assertThat(versao.estaAtiva()).isTrue();
        assertThat(versao.estaDeletada()).isFalse();
    }

    @Test
    @DisplayName("Deve detectar versão deletada (com deletadoEm)")
    void deveDetectarVersaoDeletada() {
        VersaoFichaTreino versao = new VersaoFichaTreino(ID_FICHA, 1);
        versao.setDeletadoEm(java.time.LocalDateTime.now());

        assertThat(versao.estaDeletada()).isTrue();
        assertThat(versao.estaAtiva()).isFalse();
    }
}
