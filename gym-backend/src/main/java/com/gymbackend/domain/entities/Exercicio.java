package com.gymbackend.domain.entities;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidade de domínio: Exercício de musculação.
 * Representa um exercício reutilizável na base de dados.
 * Suporta soft delete (inativação) para preservar histórico.
 */
public class Exercicio {

    private UUID id;
    private String nome;
    private String descricao;
    private String grupoMuscular;
    private StatusExercicio status;
    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;
    private LocalDateTime deletadoEm;
    private Long versao;

    public Exercicio() {}

    public Exercicio(String nome, String descricao, String grupoMuscular) {
        validarNome(nome);
        validarDescricao(descricao);
        validarGrupoMuscular(grupoMuscular);
        this.id = UUID.randomUUID();
        this.nome = nome;
        this.descricao = descricao;
        this.grupoMuscular = grupoMuscular;
        this.status = StatusExercicio.ATIVO;
        this.criadoEm = LocalDateTime.now();
        this.atualizadoEm = LocalDateTime.now();
        this.versao = 0L;
    }

    /**
     * Inativa o exercício (soft delete).
     * Preserva histórico de execuções.
     */
    public void inativar() {
        if (this.status == StatusExercicio.INATIVO) {
            throw new IllegalStateException("Exercício já está inativo.");
        }
        this.status = StatusExercicio.INATIVO;
        this.deletadoEm = LocalDateTime.now();
        this.atualizadoEm = LocalDateTime.now();
    }

    /**
     * Reativa um exercício previamente inativado.
     */
    public void reativar() {
        if (this.status == StatusExercicio.ATIVO) {
            throw new IllegalStateException("Exercício já está ativo.");
        }
        this.status = StatusExercicio.ATIVO;
        this.deletadoEm = null;
        this.atualizadoEm = LocalDateTime.now();
    }

    /**
     * Atualiza nome e descrição do exercício.
     */
    public void atualizar(String novoNome, String novaDescricao) {
        validarNome(novoNome);
        validarDescricao(novaDescricao);
        this.nome = novoNome;
        this.descricao = novaDescricao;
        this.atualizadoEm = LocalDateTime.now();
    }

    public boolean estaAtivo() {
        return StatusExercicio.ATIVO.equals(this.status);
    }

    // === Validações de domínio ===

    private void validarNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome do exercício é obrigatório.");
        }
        if (nome.length() > 255) {
            throw new IllegalArgumentException("Nome do exercício não pode exceder 255 caracteres.");
        }
    }

    private void validarDescricao(String descricao) {
        if (descricao == null || descricao.isBlank()) {
            throw new IllegalArgumentException("Descrição do exercício é obrigatória.");
        }
        if (descricao.length() > 1000) {
            throw new IllegalArgumentException("Descrição do exercício não pode exceder 1000 caracteres.");
        }
    }

    private void validarGrupoMuscular(String grupoMuscular) {
        if (grupoMuscular == null || grupoMuscular.isBlank()) {
            throw new IllegalArgumentException("Grupo muscular é obrigatório.");
        }
        if (grupoMuscular.length() > 100) {
            throw new IllegalArgumentException("Grupo muscular não pode exceder 100 caracteres.");
        }
    }

    // === Getters e Setters ===

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public String getGrupoMuscular() { return grupoMuscular; }
    public void setGrupoMuscular(String grupoMuscular) { this.grupoMuscular = grupoMuscular; }

    public StatusExercicio getStatus() { return status; }
    public void setStatus(StatusExercicio status) { this.status = status; }

    public LocalDateTime getCriadoEm() { return criadoEm; }
    public void setCriadoEm(LocalDateTime criadoEm) { this.criadoEm = criadoEm; }

    public LocalDateTime getAtualizadoEm() { return atualizadoEm; }
    public void setAtualizadoEm(LocalDateTime atualizadoEm) { this.atualizadoEm = atualizadoEm; }

    public LocalDateTime getDeletadoEm() { return deletadoEm; }
    public void setDeletadoEm(LocalDateTime deletadoEm) { this.deletadoEm = deletadoEm; }

    public Long getVersao() { return versao; }
    public void setVersao(Long versao) { this.versao = versao; }

    public enum StatusExercicio {
        ATIVO, INATIVO
    }
}
