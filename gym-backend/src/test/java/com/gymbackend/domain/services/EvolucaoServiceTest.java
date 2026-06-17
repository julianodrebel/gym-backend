package com.gymbackend.domain.services;

import com.gymbackend.TesteUnitarioBase;
import com.gymbackend.domain.entities.SerieExecutada;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

/**
 * Testes unitários para EvolucaoService.
 * Valida cálculos de volume, tendência e progressão.
 */
@DisplayName("EvolucaoService - Cálculos de Evolução")
class EvolucaoServiceTest extends TesteUnitarioBase {

    private EvolucaoService evolucaoService;

    @BeforeEach
    void setup() {
        evolucaoService = new EvolucaoService();
    }

    @Test
    @DisplayName("Volume = peso × reps para cada série")
    void deveCalcularVolumeTotal() {
        List<SerieExecutada> series = List.of(
                serie(new BigDecimal("60"), 10),  // 600
                serie(new BigDecimal("60"), 9),   // 540
                serie(new BigDecimal("60"), 8)    // 480
        );

        BigDecimal volume = evolucaoService.calcularVolumeTotal(series);

        assertThat(volume).isEqualByComparingTo(new BigDecimal("1620"));
    }

    @Test
    @DisplayName("Carga máxima deve retornar o maior peso")
    void deveCalcularCargaMaxima() {
        List<SerieExecutada> series = List.of(
                serie(new BigDecimal("55"), 10),
                serie(new BigDecimal("60"), 9),
                serie(new BigDecimal("57.5"), 8)
        );

        BigDecimal maxima = evolucaoService.calcularCargaMaxima(series);

        assertThat(maxima).isEqualByComparingTo(new BigDecimal("60"));
    }

    @Test
    @DisplayName("Tendência CRESCENTE quando diferença > +2.5kg")
    void tendenciaCrescenteQuandoDiferencaMaiorQue2_5() {
        EvolucaoService.TendenciaCarga tendencia = evolucaoService.calcularTendencia(
                new BigDecimal("55"), new BigDecimal("60"));

        assertThat(tendencia).isEqualTo(EvolucaoService.TendenciaCarga.CRESCENTE);
    }

    @Test
    @DisplayName("Tendência ESTÁVEL quando diferença entre -2.5 e +2.5")
    void tendenciaEstavelQuandoDiferencaDentroDoLimite() {
        EvolucaoService.TendenciaCarga tendencia = evolucaoService.calcularTendencia(
                new BigDecimal("60"), new BigDecimal("62"));

        assertThat(tendencia).isEqualTo(EvolucaoService.TendenciaCarga.ESTAVEL);
    }

    @Test
    @DisplayName("Tendência DECRESCENTE quando diferença < -2.5kg")
    void tendenciaDecrescenteQuandoDiferencaMenorQueMinus2_5() {
        EvolucaoService.TendenciaCarga tendencia = evolucaoService.calcularTendencia(
                new BigDecimal("60"), new BigDecimal("55"));

        assertThat(tendencia).isEqualTo(EvolucaoService.TendenciaCarga.DECRESCENTE);
    }

    @Test
    @DisplayName("Média de repetições deve calcular corretamente")
    void deveCalcularMediaRepeticoes() {
        List<SerieExecutada> series = List.of(
                serie(PESO_60KG, 10),
                serie(PESO_60KG, 9),
                serie(PESO_60KG, 8)
        );

        double media = evolucaoService.calcularMediaRepeticoes(series);

        assertThat(media).isCloseTo(9.0, within(0.01));
    }

    @Test
    @DisplayName("Lista vazia de séries retorna volume zero")
    void listaVaziaRetornaVolumeZero() {
        BigDecimal volume = evolucaoService.calcularVolumeTotal(List.of());
        assertThat(volume).isEqualByComparingTo(BigDecimal.ZERO);
    }

    private SerieExecutada serie(BigDecimal peso, int reps) {
        return new SerieExecutada(UUID.randomUUID(), 1, peso, reps);
    }
}
