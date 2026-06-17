package com.gymbackend.domain.services;

import com.gymbackend.TesteUnitarioBase;
import com.gymbackend.domain.entities.SerieExecutada;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

/**
 * Testes unitários para TempoDescansoService.
 * Valida cálculo de metadados do cronômetro client-side.
 */
@DisplayName("TempoDescansoService - Cronômetro de Descanso")
class TempoDescansoServiceTest extends TesteUnitarioBase {

    private TempoDescansoService tempoDescansoService;

    @BeforeEach
    void setup() {
        tempoDescansoService = new TempoDescansoService();
    }

    @Test
    @DisplayName("Sem série anterior, deve retornar 60s sem aviso")
    void semSerieAnteriorRetornaSemAviso() {
        TempoDescansoService.CronoMetadata meta = tempoDescansoService.calcularMetadata(null);

        assertThat(meta.getTempoDescansoRecomendado()).isEqualTo(60);
        assertThat(meta.isIniciarCrono()).isTrue();
        assertThat(meta.isAvisoIntervaloInsuficiente()).isFalse();
    }

    @Test
    @DisplayName("Deve emitir aviso se intervalo < 30 segundos")
    void deveEmitirAvisoSeIntervaloMenorQue30s() {
        SerieExecutada serieRecente = new SerieExecutada(UUID.randomUUID(), 1, PESO_60KG, REPS_10);
        // Horário de execução = agora - 10 segundos (menos que 30s)
        serieRecente.setHorarioExecucao(LocalDateTime.now().minusSeconds(10));

        TempoDescansoService.CronoMetadata meta = tempoDescansoService.calcularMetadata(serieRecente);

        assertThat(meta.isAvisoIntervaloInsuficiente()).isTrue();
    }

    @Test
    @DisplayName("Não deve emitir aviso se intervalo >= 30 segundos")
    void naoDeveEmitirAvisoSeIntervaloSuficiente() {
        SerieExecutada serieAntiga = new SerieExecutada(UUID.randomUUID(), 1, PESO_60KG, REPS_10);
        serieAntiga.setHorarioExecucao(LocalDateTime.now().minusSeconds(60));

        TempoDescansoService.CronoMetadata meta = tempoDescansoService.calcularMetadata(serieAntiga);

        assertThat(meta.isAvisoIntervaloInsuficiente()).isFalse();
    }

    @Test
    @DisplayName("Tempo personalizado deve substituir padrão")
    void tempoPersonalizadoDeveSubstituirPadrao() {
        TempoDescansoService.CronoMetadata meta =
                tempoDescansoService.calcularMetadata(null, 90);

        assertThat(meta.getTempoDescansoRecomendado()).isEqualTo(90);
    }
}
