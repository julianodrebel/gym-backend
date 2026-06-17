package com.gymbackend.application.dto;

import java.time.LocalDate;
import java.util.UUID;

/**
 * DTO para histórico de utilização de um exercício.
 */
public class HistoricoExercicioDTO {

    private UUID exercicioId;
    private String nomeExercicio;
    private LocalDate primeiraExecucao;
    private LocalDate ultimaExecucao;
    private Long totalSeries;

    public HistoricoExercicioDTO() {}

    public UUID getExercicioId() { return exercicioId; }
    public void setExercicioId(UUID exercicioId) { this.exercicioId = exercicioId; }

    public String getNomeExercicio() { return nomeExercicio; }
    public void setNomeExercicio(String nomeExercicio) { this.nomeExercicio = nomeExercicio; }

    public LocalDate getPrimeiraExecucao() { return primeiraExecucao; }
    public void setPrimeiraExecucao(LocalDate primeiraExecucao) { this.primeiraExecucao = primeiraExecucao; }

    public LocalDate getUltimaExecucao() { return ultimaExecucao; }
    public void setUltimaExecucao(LocalDate ultimaExecucao) { this.ultimaExecucao = ultimaExecucao; }

    public Long getTotalSeries() { return totalSeries; }
    public void setTotalSeries(Long totalSeries) { this.totalSeries = totalSeries; }
}
