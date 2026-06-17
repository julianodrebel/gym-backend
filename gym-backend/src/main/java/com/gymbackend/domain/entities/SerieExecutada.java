package com.gymbackend.domain.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidade de domínio: Série Executada.
 * Representa uma série realizada durante a execução de um exercício.
 * Imutável após criação - representa um fato histórico.
 */
public class SerieExecutada {

    private UUID id;
    private UUID exercicioExecutadoId;
    private Integer numeroSerie;
    private BigDecimal peso;
    private Integer repeticoes;
    private LocalDateTime horarioExecucao;
    private Long versao;

    public SerieExecutada() {}

    public SerieExecutada(UUID exercicioExecutadoId, Integer numeroSerie, BigDecimal peso, Integer repeticoes) {
        validarExercicioExecutadoId(exercicioExecutadoId);
        validarNumeroSerie(numeroSerie);
        validarPeso(peso);
        validarRepeticoes(repeticoes);

        this.id = UUID.randomUUID();
        this.exercicioExecutadoId = exercicioExecutadoId;
        this.numeroSerie = numeroSerie;
        this.peso = peso;
        this.repeticoes = repeticoes;
        this.horarioExecucao = LocalDateTime.now();
        this.versao = 0L;
    }

    /**
     * Calcula volume da série (peso × repetições).
     */
    public BigDecimal calcularVolume() {
        return peso.multiply(BigDecimal.valueOf(repeticoes));
    }

    // === Validações de domínio ===

    private void validarExercicioExecutadoId(UUID exercicioExecutadoId) {
        if (exercicioExecutadoId == null) {
            throw new IllegalArgumentException("ID do exercício executado é obrigatório.");
        }
    }

    private void validarNumeroSerie(Integer numeroSerie) {
        if (numeroSerie == null || numeroSerie < 1) {
            throw new IllegalArgumentException("Número da série deve ser >= 1.");
        }
    }

    private void validarPeso(BigDecimal peso) {
        if (peso == null) {
            throw new IllegalArgumentException("Peso é obrigatório.");
        }
        if (peso.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Peso não pode ser negativo.");
        }
        if (peso.compareTo(BigDecimal.valueOf(500)) > 0) {
            throw new IllegalArgumentException("Peso não pode exceder 500kg.");
        }
    }

    private void validarRepeticoes(Integer repeticoes) {
        if (repeticoes == null) {
            throw new IllegalArgumentException("Repetições são obrigatórias.");
        }
        if (repeticoes < 0) {
            throw new IllegalArgumentException("Repetições não podem ser negativas.");
        }
        if (repeticoes > 100) {
            throw new IllegalArgumentException("Repetições não podem exceder 100.");
        }
    }

    // === Getters e Setters ===

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getExercicioExecutadoId() { return exercicioExecutadoId; }
    public void setExercicioExecutadoId(UUID exercicioExecutadoId) { this.exercicioExecutadoId = exercicioExecutadoId; }

    public Integer getNumeroSerie() { return numeroSerie; }
    public void setNumeroSerie(Integer numeroSerie) { this.numeroSerie = numeroSerie; }

    public BigDecimal getPeso() { return peso; }
    public void setPeso(BigDecimal peso) { this.peso = peso; }

    public Integer getRepeticoes() { return repeticoes; }
    public void setRepeticoes(Integer repeticoes) { this.repeticoes = repeticoes; }

    public LocalDateTime getHorarioExecucao() { return horarioExecucao; }
    public void setHorarioExecucao(LocalDateTime horarioExecucao) { this.horarioExecucao = horarioExecucao; }

    public Long getVersao() { return versao; }
    public void setVersao(Long versao) { this.versao = versao; }
}
