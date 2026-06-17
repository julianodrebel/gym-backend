package com.gymbackend.domain.entities;

import com.gymbackend.TesteUnitarioBase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * Testes unitários para a entidade Exercicio.
 * Valida validações de domínio e transições de estado.
 */
@DisplayName("Exercicio - Validações de Domínio")
class ExercicioTest extends TesteUnitarioBase {

    @Test
    @DisplayName("Deve criar exercício com dados válidos")
    void deveCriarExercicioComDadosValidos() {
        Exercicio ex = new Exercicio("Supino Reto", "Exercício básico de peito", "Peito");

        assertThat(ex.getId()).isNotNull();
        assertThat(ex.getNome()).isEqualTo("Supino Reto");
        assertThat(ex.getStatus()).isEqualTo(Exercicio.StatusExercicio.ATIVO);
        assertThat(ex.getCriadoEm()).isNotNull();
        assertThat(ex.getDeletadoEm()).isNull();
    }

    @Test
    @DisplayName("Deve rejeitar nome em branco")
    void deveRejeitarNomeEmBranco() {
        assertThatThrownBy(() -> new Exercicio("", "Descrição", "Peito"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Nome do exercício é obrigatório");
    }

    @Test
    @DisplayName("Deve rejeitar nome com mais de 255 caracteres")
    void deveRejeitarNomeMuitoLongo() {
        String nomeLongo = "A".repeat(256);
        assertThatThrownBy(() -> new Exercicio(nomeLongo, "Descrição", "Peito"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("255");
    }

    @Test
    @DisplayName("Deve rejeitar descrição em branco")
    void deveRejeitarDescricaoEmBranco() {
        assertThatThrownBy(() -> new Exercicio("Supino", "", "Peito"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Descrição do exercício é obrigatória");
    }

    @Test
    @DisplayName("Deve rejeitar descrição com mais de 1000 caracteres")
    void deveRejeitarDescricaoMuitoLonga() {
        String descLonga = "A".repeat(1001);
        assertThatThrownBy(() -> new Exercicio("Supino", descLonga, "Peito"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("1000");
    }

    @Test
    @DisplayName("Deve inativar exercício ativo")
    void deveInativarExercicioAtivo() {
        Exercicio ex = new Exercicio("Supino Reto", "Descrição", "Peito");

        ex.inativar();

        assertThat(ex.getStatus()).isEqualTo(Exercicio.StatusExercicio.INATIVO);
        assertThat(ex.getDeletadoEm()).isNotNull();
    }

    @Test
    @DisplayName("Deve rejeitar inativar exercício já inativo")
    void deveRejeitarInativarExercicioJaInativo() {
        Exercicio ex = new Exercicio("Supino Reto", "Descrição", "Peito");
        ex.inativar();

        assertThatThrownBy(ex::inativar)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("já está inativo");
    }

    @Test
    @DisplayName("Deve reativar exercício inativo")
    void deveReativarExercicioInativo() {
        Exercicio ex = new Exercicio("Supino Reto", "Descrição", "Peito");
        ex.inativar();

        ex.reativar();

        assertThat(ex.getStatus()).isEqualTo(Exercicio.StatusExercicio.ATIVO);
        assertThat(ex.getDeletadoEm()).isNull();
    }

    @Test
    @DisplayName("Deve atualizar nome e descrição")
    void deveAtualizarNomeEDescricao() {
        Exercicio ex = new Exercicio("Supino Reto", "Descrição original", "Peito");

        ex.atualizar("Supino Inclinado", "Nova descrição");

        assertThat(ex.getNome()).isEqualTo("Supino Inclinado");
        assertThat(ex.getDescricao()).isEqualTo("Nova descrição");
        assertThat(ex.getAtualizadoEm()).isNotNull();
    }

    @Test
    @DisplayName("estaAtivo deve retornar true para exercício ativo")
    void estaAtivoDeveRetornarTrueParaAtivo() {
        Exercicio ex = new Exercicio("Supino", "Desc", "Peito");
        assertThat(ex.estaAtivo()).isTrue();
    }
}
