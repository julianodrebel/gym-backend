package com.gymbackend.domain.services;

import com.gymbackend.domain.entities.SerieExecutada;
import com.gymbackend.domain.repositories.ExercicioExecutadoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Serviço de domínio: Progressão Automática de Carga.
 * Implementa regra 8-12 RMs (Repetição Máxima) baseada em literatura científica de treino de força.
 *
 * Regras:
 * - Todas séries >= 12 reps → AUMENTAR_CARGA (+2.5kg)
 * - Alguma série < 8 reps → MANTER_CARGA
 * - 8-12 reps → MANTER_CARGA (progredir repetições)
 * - Sem histórico → SEM_HISTORICO
 */
@ApplicationScoped
public class ProgressaoService {

    private static final int REPS_MINIMO = 8;
    private static final int REPS_MAXIMO = 12;
    private static final BigDecimal INCREMENTO_PADRAO = new BigDecimal("2.5");
    private static final int NUMERO_SERIES_AVALIADAS = 5;

    @Inject
    ExercicioExecutadoRepository exercicioExecRepository;

    /**
     * Calcula sugestão de progressão para um exercício.
     */
    public SugestaoProgressao calcular(UUID exercicioId) {
        List<SerieExecutada> ultimasSeries =
                exercicioExecRepository.buscarUltimasSeriesPorExercicio(exercicioId, NUMERO_SERIES_AVALIADAS);

        if (ultimasSeries.isEmpty()) {
            return SugestaoProgressao.semHistorico();
        }

        int minReps = ultimasSeries.stream()
                .mapToInt(SerieExecutada::getRepeticoes)
                .min()
                .orElse(0);

        BigDecimal cargaMaxima = ultimasSeries.stream()
                .map(SerieExecutada::getPeso)
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);

        if (minReps >= REPS_MAXIMO) {
            BigDecimal cargaSugerida = cargaMaxima.add(INCREMENTO_PADRAO);
            return SugestaoProgressao.aumentarCarga(cargaMaxima, cargaSugerida, ultimasSeries);
        } else if (minReps < REPS_MINIMO) {
            return SugestaoProgressao.manterCarga(cargaMaxima, ultimasSeries,
                    "Alguma série ficou abaixo de " + REPS_MINIMO + " reps. Consolide a carga atual.");
        } else {
            return SugestaoProgressao.manterCarga(cargaMaxima, ultimasSeries,
                    "Séries dentro da faixa " + REPS_MINIMO + "-" + REPS_MAXIMO + ". Continue progredindo repetições.");
        }
    }

    /**
     * Resultado da sugestão de progressão.
     */
    public static class SugestaoProgressao {

        public enum TipoSugestao {
            AUMENTAR_CARGA, MANTER_CARGA, SEM_HISTORICO
        }

        private final TipoSugestao sugestao;
        private final BigDecimal cargaAtual;
        private final BigDecimal cargaSugerida;
        private final String justificativa;
        private final List<SerieExecutada> ultimasSeries;

        private SugestaoProgressao(TipoSugestao sugestao, BigDecimal cargaAtual,
                                   BigDecimal cargaSugerida, String justificativa,
                                   List<SerieExecutada> ultimasSeries) {
            this.sugestao = sugestao;
            this.cargaAtual = cargaAtual;
            this.cargaSugerida = cargaSugerida;
            this.justificativa = justificativa;
            this.ultimasSeries = ultimasSeries;
        }

        public static SugestaoProgressao aumentarCarga(BigDecimal atual, BigDecimal sugerida,
                                                       List<SerieExecutada> series) {
            return new SugestaoProgressao(TipoSugestao.AUMENTAR_CARGA, atual, sugerida,
                    "Todas as últimas séries atingiram >= 12 reps. Aumente a carga!", series);
        }

        public static SugestaoProgressao manterCarga(BigDecimal atual, List<SerieExecutada> series,
                                                     String justificativa) {
            return new SugestaoProgressao(TipoSugestao.MANTER_CARGA, atual, atual, justificativa, series);
        }

        public static SugestaoProgressao semHistorico() {
            return new SugestaoProgressao(TipoSugestao.SEM_HISTORICO, null, null,
                    "Sem histórico - comece com carga confortável.", List.of());
        }

        public TipoSugestao getSugestao() { return sugestao; }
        public BigDecimal getCargaAtual() { return cargaAtual; }
        public BigDecimal getCargaSugerida() { return cargaSugerida; }
        public String getJustificativa() { return justificativa; }
        public List<SerieExecutada> getUltimasSeries() { return ultimasSeries; }
    }
}
