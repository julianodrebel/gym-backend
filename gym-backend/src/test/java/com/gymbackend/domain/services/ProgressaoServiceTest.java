package com.gymbackend.domain.services;

import com.gymbackend.TesteUnitarioBase;
import com.gymbackend.domain.entities.SerieExecutada;
import com.gymbackend.domain.repositories.ExercicioExecutadoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para o algoritmo de progressão 8-12 RMs.
 * Valida todas as regras de negócio da sugestão de progressão.
 */
@DisplayName("ProgressaoService - Algoritmo 8-12 RMs")
class ProgressaoServiceTest extends TesteUnitarioBase {

    @Mock
    ExercicioExecutadoRepository repository;

    @InjectMocks
    ProgressaoService progressaoService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Todas séries >= 12 reps → AUMENTAR_CARGA (+2.5kg)")
    void todasSeriesAcima12RepsSugerirAumentarCarga() {
        List<SerieExecutada> series = List.of(
                serie(PESO_60KG, 12),
                serie(PESO_60KG, 13),
                serie(PESO_60KG, 12)
        );
        when(repository.buscarUltimasSeriesPorExercicio(ID_EXERCICIO, 5)).thenReturn(series);

        ProgressaoService.SugestaoProgressao sugestao = progressaoService.calcular(ID_EXERCICIO);

        assertThat(sugestao.getSugestao())
                .isEqualTo(ProgressaoService.SugestaoProgressao.TipoSugestao.AUMENTAR_CARGA);
        assertThat(sugestao.getCargaSugerida())
                .isEqualByComparingTo(new BigDecimal("62.5"));
        assertThat(sugestao.getJustificativa()).contains("12 reps");
    }

    @Test
    @DisplayName("Alguma série < 8 reps → MANTER_CARGA")
    void algumaSerieAbaixo8RepsMantercarga() {
        List<SerieExecutada> series = List.of(
                serie(PESO_60KG, 10),
                serie(PESO_60KG, 7),  // Abaixo do mínimo
                serie(PESO_60KG, 9)
        );
        when(repository.buscarUltimasSeriesPorExercicio(ID_EXERCICIO, 5)).thenReturn(series);

        ProgressaoService.SugestaoProgressao sugestao = progressaoService.calcular(ID_EXERCICIO);

        assertThat(sugestao.getSugestao())
                .isEqualTo(ProgressaoService.SugestaoProgressao.TipoSugestao.MANTER_CARGA);
        assertThat(sugestao.getJustificativa()).contains("8");
    }

    @Test
    @DisplayName("Séries entre 8-12 reps → MANTER_CARGA (progredir reps)")
    void series8A12RepsMantercarga() {
        List<SerieExecutada> series = List.of(
                serie(PESO_60KG, 10),
                serie(PESO_60KG, 9),
                serie(PESO_60KG, 8)
        );
        when(repository.buscarUltimasSeriesPorExercicio(ID_EXERCICIO, 5)).thenReturn(series);

        ProgressaoService.SugestaoProgressao sugestao = progressaoService.calcular(ID_EXERCICIO);

        assertThat(sugestao.getSugestao())
                .isEqualTo(ProgressaoService.SugestaoProgressao.TipoSugestao.MANTER_CARGA);
        assertThat(sugestao.getJustificativa()).contains("8-12");
    }

    @Test
    @DisplayName("Sem histórico → SEM_HISTORICO")
    void semHistoricoRetornaSemHistorico() {
        when(repository.buscarUltimasSeriesPorExercicio(ID_EXERCICIO, 5)).thenReturn(List.of());

        ProgressaoService.SugestaoProgressao sugestao = progressaoService.calcular(ID_EXERCICIO);

        assertThat(sugestao.getSugestao())
                .isEqualTo(ProgressaoService.SugestaoProgressao.TipoSugestao.SEM_HISTORICO);
        assertThat(sugestao.getJustificativa()).contains("Sem histórico");
    }

    private SerieExecutada serie(BigDecimal peso, int reps) {
        SerieExecutada s = new SerieExecutada(UUID.randomUUID(), 1, peso, reps);
        return s;
    }
}
