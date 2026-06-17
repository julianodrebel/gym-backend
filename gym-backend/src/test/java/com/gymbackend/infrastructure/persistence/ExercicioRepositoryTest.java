package com.gymbackend.infrastructure.persistence;

import com.gymbackend.TesteIntegracaoBase;
import com.gymbackend.domain.entities.Exercicio;
import com.gymbackend.domain.repositories.ExercicioRepository;
import com.gymbackend.fixtures.ExercicioFixture;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

/**
 * Testes de integração para ExercicioRepository.
 * Usa H2 em memória com perfil %test.
 */
@DisplayName("ExercicioRepository - Integração com H2")
class ExercicioRepositoryTest extends TesteIntegracaoBase {

    @Inject
    ExercicioRepository exercicioRepository;

    @Test
    @Transactional
    @DisplayName("Salvar e buscar exercício por ID")
    void salvarEBuscarPorId() {
        Exercicio exercicio = ExercicioFixture.exercicioValido();
        Exercicio salvo = exercicioRepository.salvar(exercicio);

        Optional<Exercicio> encontrado = exercicioRepository.buscarPorId(salvo.getId());

        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getNome()).isEqualTo("Supino Reto");
        assertThat(encontrado.get().getStatus()).isEqualTo(Exercicio.StatusExercicio.ATIVO);
    }

    @Test
    @Transactional
    @DisplayName("Buscar ID inexistente retorna Optional vazio")
    void buscarIdInexistenteRetornaVazio() {
        Optional<Exercicio> resultado = exercicioRepository.buscarPorId(UUID.randomUUID());
        assertThat(resultado).isEmpty();
    }

    @Test
    @Transactional
    @DisplayName("Listar ativos retorna apenas exercícios ATIVOS")
    void listarAtivosRetornaApenasAtivos() {
        Exercicio ativo = ExercicioFixture.exercicioComNome("Agachamento Livre");
        Exercicio salvo = exercicioRepository.salvar(ativo);
        salvo.inativar();
        exercicioRepository.salvar(salvo);

        List<Exercicio> ativos = exercicioRepository.listarAtivos(0, 10);

        assertThat(ativos).noneMatch(e -> e.getId().equals(salvo.getId()));
    }

    @Test
    @Transactional
    @DisplayName("Nome duplicado é detectado por existePorNome")
    void nomeDuplicadoDetectado() {
        Exercicio exercicio = ExercicioFixture.exercicioComNome("Terra");
        exercicioRepository.salvar(exercicio);

        assertThat(exercicioRepository.existePorNome("Terra")).isTrue();
        assertThat(exercicioRepository.existePorNome("Leg Press")).isFalse();
    }
}
