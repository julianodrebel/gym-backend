package com.gymbackend.domain.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Entidade de domínio: Exercício Executado.
 * Representa a execução de um exercício específico durante uma sessão de treino.
 */
public class ExercicioExecutado {

    private UUID id;
    private UUID sessaoId;
    private UUID exercicioId;
    private Exercicio exercicio;
    private Integer ordem;
    private String observacoes;
    private Integer duracaoMinutos;
    private List<SerieExecutada> series;

    public ExercicioExecutado() {
        this.series = new ArrayList<>();
    }

    public ExercicioExecutado(UUID sessaoId, UUID exercicioId, Integer ordem) {
        if (sessaoId == null) {
            throw new IllegalArgumentException("ID da sessão é obrigatório.");
        }
        if (exercicioId == null) {
            throw new IllegalArgumentException("ID do exercício é obrigatório.");
        }
        if (ordem == null || ordem < 0) {
            throw new IllegalArgumentException("Ordem deve ser >= 0.");
        }
        this.id = UUID.randomUUID();
        this.sessaoId = sessaoId;
        this.exercicioId = exercicioId;
        this.ordem = ordem;
        this.series = new ArrayList<>();
    }

    /**
     * Adiciona observações ao exercício executado.
     */
    public void adicionarObservacoes(String observacoes) {
        if (observacoes != null && observacoes.length() > 1000) {
            throw new IllegalArgumentException("Observações não podem exceder 1000 caracteres.");
        }
        this.observacoes = observacoes;
    }

    /**
     * Retorna a última série executada, se existir.
     */
    public SerieExecutada obterUltimaSerie() {
        if (series.isEmpty()) {
            return null;
        }
        return series.get(series.size() - 1);
    }

    /**
     * Retorna o próximo número de série baseado nas séries existentes.
     */
    public int proximoNumeroSerie() {
        return series.size() + 1;
    }

    // === Getters e Setters ===

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getSessaoId() { return sessaoId; }
    public void setSessaoId(UUID sessaoId) { this.sessaoId = sessaoId; }

    public UUID getExercicioId() { return exercicioId; }
    public void setExercicioId(UUID exercicioId) { this.exercicioId = exercicioId; }

    public Exercicio getExercicio() { return exercicio; }
    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
        if (exercicio != null) {
            this.exercicioId = exercicio.getId();
        }
    }

    public Integer getOrdem() { return ordem; }
    public void setOrdem(Integer ordem) { this.ordem = ordem; }

    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }

    public Integer getDuracaoMinutos() { return duracaoMinutos; }
    public void setDuracaoMinutos(Integer duracaoMinutos) { this.duracaoMinutos = duracaoMinutos; }

    public List<SerieExecutada> getSeries() { return series; }
    public void setSeries(List<SerieExecutada> series) { this.series = series; }
}
