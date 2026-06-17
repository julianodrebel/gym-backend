package com.gymbackend.domain.services;

import com.gymbackend.domain.entities.SerieExecutada;
import jakarta.enterprise.context.ApplicationScoped;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Serviço de domínio: Cálculo de Evolução de Desempenho.
 * Calcula volume, tendência e progressão ao longo do tempo.
 */
@ApplicationScoped
public class EvolucaoService {

    /**
     * Calcula volume total de uma lista de séries.
     * Volume = soma(peso × repetições)
     */
    public BigDecimal calcularVolumeTotal(List<SerieExecutada> series) {
        return series.stream()
                .map(s -> s.getPeso().multiply(BigDecimal.valueOf(s.getRepeticoes())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Calcula carga máxima de uma lista de séries.
     */
    public BigDecimal calcularCargaMaxima(List<SerieExecutada> series) {
        return series.stream()
                .map(SerieExecutada::getPeso)
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
    }

    /**
     * Determina tendência com base em cargas de dois períodos.
     * CRESCENTE: diferença > +2.5kg
     * DECRESCENTE: diferença < -2.5kg
     * ESTÁVEL: diferença entre -2.5 e +2.5
     */
    public TendenciaCarga calcularTendencia(BigDecimal cargaAnterior, BigDecimal cargaAtual) {
        if (cargaAnterior == null || cargaAnterior.compareTo(BigDecimal.ZERO) == 0) {
            return TendenciaCarga.ESTAVEL;
        }

        BigDecimal diferenca = cargaAtual.subtract(cargaAnterior);
        BigDecimal limite = new BigDecimal("2.5");

        if (diferenca.compareTo(limite) > 0) {
            return TendenciaCarga.CRESCENTE;
        } else if (diferenca.negate().compareTo(limite) > 0) {
            return TendenciaCarga.DECRESCENTE;
        } else {
            return TendenciaCarga.ESTAVEL;
        }
    }

    /**
     * Calcula a média de repetições de uma lista de séries.
     */
    public double calcularMediaRepeticoes(List<SerieExecutada> series) {
        if (series.isEmpty()) return 0;
        return series.stream()
                .mapToInt(SerieExecutada::getRepeticoes)
                .average()
                .orElse(0);
    }

    /**
     * Calcula percentual de melhora entre dois valores.
     */
    public String calcularPercentualMelhora(BigDecimal anterior, BigDecimal atual) {
        if (anterior == null || anterior.compareTo(BigDecimal.ZERO) == 0) {
            return "N/A";
        }
        BigDecimal percentual = atual.subtract(anterior)
                .multiply(BigDecimal.valueOf(100))
                .divide(anterior, 1, RoundingMode.HALF_UP);
        BigDecimal diferenca = atual.subtract(anterior);
        String sinal = diferenca.compareTo(BigDecimal.ZERO) >= 0 ? "+" : "";
        return sinal + diferenca + "kg (" + sinal + percentual + "%)";
    }

    public enum TendenciaCarga {
        CRESCENTE, ESTAVEL, DECRESCENTE
    }
}
