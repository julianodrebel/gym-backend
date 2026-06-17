package com.gymbackend.domain.services;

import com.gymbackend.domain.entities.SerieExecutada;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Serviço de domínio: Cálculo de Tempo de Descanso.
 * Implementa cronômetro client-side com validação server-side.
 *
 * Decisão de design: Cronômetro é controlado pelo cliente.
 * Servidor fornece metadados e valida intervalos mínimos.
 */
@ApplicationScoped
public class TempoDescansoService {

    private static final int TEMPO_DESCANSO_PADRAO_SEGUNDOS = 60;
    private static final int INTERVALO_MINIMO_SEGUNDOS = 30;

    /**
     * Retorna metadados do cronômetro para resposta do servidor.
     */
    public CronoMetadata calcularMetadata(SerieExecutada ultimaSerie) {
        boolean avisoIntervaloInsuficiente = false;

        if (ultimaSerie != null) {
            long segundosDesde = Duration.between(
                    ultimaSerie.getHorarioExecucao(),
                    LocalDateTime.now()
            ).getSeconds();

            avisoIntervaloInsuficiente = segundosDesde < INTERVALO_MINIMO_SEGUNDOS;
        }

        return new CronoMetadata(TEMPO_DESCANSO_PADRAO_SEGUNDOS, true, avisoIntervaloInsuficiente);
    }

    /**
     * Calcula metadados com tempo personalizado por exercício.
     */
    public CronoMetadata calcularMetadata(SerieExecutada ultimaSerie, int tempoPersonalizadoSegundos) {
        boolean avisoIntervaloInsuficiente = false;

        if (ultimaSerie != null) {
            long segundosDesde = Duration.between(
                    ultimaSerie.getHorarioExecucao(),
                    LocalDateTime.now()
            ).getSeconds();

            avisoIntervaloInsuficiente = segundosDesde < INTERVALO_MINIMO_SEGUNDOS;
        }

        return new CronoMetadata(tempoPersonalizadoSegundos, true, avisoIntervaloInsuficiente);
    }

    /**
     * Metadados do cronômetro retornados ao cliente.
     */
    public static class CronoMetadata {
        private final int tempoDescansoRecomendado;
        private final boolean iniciarCrono;
        private final boolean avisoIntervaloInsuficiente;

        public CronoMetadata(int tempoDescansoRecomendado, boolean iniciarCrono,
                             boolean avisoIntervaloInsuficiente) {
            this.tempoDescansoRecomendado = tempoDescansoRecomendado;
            this.iniciarCrono = iniciarCrono;
            this.avisoIntervaloInsuficiente = avisoIntervaloInsuficiente;
        }

        public int getTempoDescansoRecomendado() { return tempoDescansoRecomendado; }
        public boolean isIniciarCrono() { return iniciarCrono; }
        public boolean isAvisoIntervaloInsuficiente() { return avisoIntervaloInsuficiente; }
    }
}
