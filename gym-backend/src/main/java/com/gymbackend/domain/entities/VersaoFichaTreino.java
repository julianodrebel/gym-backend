package com.gymbackend.domain.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Entidade de domínio: Versão de Ficha de Treino.
 * CRÍTICA: Representa uma versão IMUTÁVEL de uma ficha de treino.
 * Nenhuma versão pode ser alterada após criação.
 * Cada modificação na ficha cria uma NOVA versão.
 */
public class VersaoFichaTreino {

    private UUID id;
    private UUID fichaTreinoId;
    private Integer numero;
    private List<UUID> exerciciosIds;
    private List<ExercicioOrdem> exercicios;
    private LocalDateTime criadoEm;
    private LocalDateTime deletadoEm;
    private Long versao;

    public VersaoFichaTreino() {
        this.exerciciosIds = new ArrayList<>();
        this.exercicios = new ArrayList<>();
    }

    public VersaoFichaTreino(UUID fichaTreinoId, Integer numero) {
        if (fichaTreinoId == null) {
            throw new IllegalArgumentException("ID da ficha de treino é obrigatório.");
        }
        if (numero == null || numero < 1) {
            throw new IllegalArgumentException("Número da versão deve ser >= 1.");
        }
        this.id = UUID.randomUUID();
        this.fichaTreinoId = fichaTreinoId;
        this.numero = numero;
        this.exerciciosIds = new ArrayList<>();
        this.exercicios = new ArrayList<>();
        this.criadoEm = LocalDateTime.now();
        this.versao = 0L;
    }

    /**
     * Valida que esta versão NÃO pode ser modificada.
     * Versões são imutáveis por design.
     *
     * @throws IllegalStateException sempre que chamado, indicando tentativa de modificação.
     */
    public void validarNaoModificavel() {
        throw new IllegalStateException(
            "Versão " + this.numero + " é imutável. Para alterar exercícios, crie uma nova versão.");
    }

    /**
     * Verifica se a versão está deletada (soft delete).
     */
    public boolean estaDeletada() {
        return this.deletadoEm != null;
    }

    /**
     * Verifica se a versão está ativa (não deletada).
     */
    public boolean estaAtiva() {
        return this.deletadoEm == null;
    }

    // === Getters e Setters ===

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getFichaTreinoId() { return fichaTreinoId; }
    public void setFichaTreinoId(UUID fichaTreinoId) { this.fichaTreinoId = fichaTreinoId; }

    public Integer getNumero() { return numero; }
    public void setNumero(Integer numero) { this.numero = numero; }

    public List<UUID> getExerciciosIds() { return exerciciosIds; }
    public void setExerciciosIds(List<UUID> exerciciosIds) { this.exerciciosIds = exerciciosIds; }

    public List<ExercicioOrdem> getExercicios() { return exercicios; }
    public void setExercicios(List<ExercicioOrdem> exercicios) { this.exercicios = exercicios; }

    public LocalDateTime getCriadoEm() { return criadoEm; }
    public void setCriadoEm(LocalDateTime criadoEm) { this.criadoEm = criadoEm; }

    public LocalDateTime getDeletadoEm() { return deletadoEm; }
    public void setDeletadoEm(LocalDateTime deletadoEm) { this.deletadoEm = deletadoEm; }

    public Long getVersao() { return versao; }
    public void setVersao(Long versao) { this.versao = versao; }

    /**
     * Classe interna para representar exercício com ordem.
     */
    public static class ExercicioOrdem {
        private UUID exercicioId;
        private Integer ordem;

        public ExercicioOrdem() {}

        public ExercicioOrdem(UUID exercicioId, Integer ordem) {
            this.exercicioId = exercicioId;
            this.ordem = ordem;
        }

        public UUID getExercicioId() { return exercicioId; }
        public void setExercicioId(UUID exercicioId) { this.exercicioId = exercicioId; }

        public Integer getOrdem() { return ordem; }
        public void setOrdem(Integer ordem) { this.ordem = ordem; }
    }
}
