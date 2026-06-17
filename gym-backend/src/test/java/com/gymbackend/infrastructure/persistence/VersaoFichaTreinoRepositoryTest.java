package com.gymbackend.infrastructure.persistence;

import com.gymbackend.TesteIntegracaoBase;
import com.gymbackend.domain.entities.FichaTreino;
import com.gymbackend.domain.entities.VersaoFichaTreino;
import com.gymbackend.domain.repositories.FichaTreinoRepository;
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
 * Testes de integração para VersaoFichaTreinoRepository.
 * Valida persistência e imutabilidade das versões.
 */
@DisplayName("VersaoFichaTreinoRepository - Integração com H2")
class VersaoFichaTreinoRepositoryTest extends TesteIntegracaoBase {

    @Inject
    FichaTreinoRepository fichaRepository;

    @Inject
    VersaoFichaTreinoRepository versaoRepository;

    @Test
    @Transactional
    @DisplayName("Criar e buscar versão por ID")
    void criarEBuscarVersao() {
        FichaTreino ficha = ExercicioFixture.fichaValida();
        FichaTreino fichaSalva = fichaRepository.salvar(ficha);

        VersaoFichaTreino versao = ExercicioFixture.versaoValida(fichaSalva.getId());
        VersaoFichaTreino versaoSalva = versaoRepository.salvar(versao);

        Optional<VersaoFichaTreino> encontrada = versaoRepository.buscarPorId(versaoSalva.getId());

        assertThat(encontrada).isPresent();
        assertThat(encontrada.get().getNumero()).isEqualTo(1);
        assertThat(encontrada.get().getFichaTreinoId()).isEqualTo(fichaSalva.getId());
    }

    @Test
    @Transactional
    @DisplayName("Listar versões de uma ficha")
    void listarVersoesDeFicha() {
        FichaTreino ficha = ExercicioFixture.fichaValida();
        FichaTreino fichaSalva = fichaRepository.salvar(ficha);

        versaoRepository.salvar(ExercicioFixture.versaoValida(fichaSalva.getId()));

        List<VersaoFichaTreino> versoes = versaoRepository.listarPorFicha(fichaSalva.getId());

        assertThat(versoes).hasSize(1);
        assertThat(versoes.get(0).getNumero()).isEqualTo(1);
    }

    @Test
    @Transactional
    @DisplayName("Contar versões de uma ficha")
    void contarVersoesDeFicha() {
        FichaTreino ficha = ExercicioFixture.fichaValida();
        FichaTreino fichaSalva = fichaRepository.salvar(ficha);

        versaoRepository.salvar(ExercicioFixture.versaoValida(fichaSalva.getId()));

        int count = versaoRepository.contarVersoesPorFicha(fichaSalva.getId());

        assertThat(count).isEqualTo(1);
    }
}
