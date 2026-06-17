package com.gymbackend.domain.entities;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Entidade de domínio: Sessão de Treino.
 * Representa a execução de uma ficha de treino num momento específico.
 * Registra qual versão da ficha foi usada (audit trail).
 */
public class SessaoTreino {

    private UUID id;
    private UUID usuarioId;
    private UUID versaoFichaTreinoId;
    private VersaoFichaTreino versaoFichaTreino;
    private LocalDate data;
    private LocalDateTime horaInicio;
    private LocalDateTime horaFim;
    private String observacoesGerais;
    private StatusSessao status;
    private List<ExercicioExecutado> exercicios;
    private Long versao;

    public SessaoTreino() {
        this.exercicios = new ArrayList<>();
    }

    public SessaoTreino(UUID usuarioId, VersaoFichaTreino versaoFichaTreino) {
        if (usuarioId == null) {
            throw new IllegalArgumentException("ID do usuário é obrigatório.");
        }
        if (versaoFichaTreino == null) {
            throw new IllegalArgumentException("Versão da ficha de treino é obrigatória.");
        }
        if (versaoFichaTreino.estaDeletada()) {
            throw new IllegalStateException("Não é possível iniciar sessão com versão deletada.");
        }
        this.id = UUID.randomUUID();
        this.usuarioId = usuarioId;
        this.versaoFichaTreino = versaoFichaTreino;
        this.versaoFichaTreinoId = versaoFichaTreino.getId();
        this.data = LocalDate.now();
        this.horaInicio = LocalDateTime.now();
        this.status = StatusSessao.EM_PROGRESSO;
        this.exercicios = new ArrayList<>();
        this.versao = 0L;
    }

    /**
     * Finaliza a sessão de treino com observações gerais.
     */
    public void finalizar(String observacoes) {
        if (this.status != StatusSessao.EM_PROGRESSO) {
            throw new IllegalStateException("Sessão não está em progresso. Status atual: " + this.status);
        }
        this.status = StatusSessao.FINALIZADO;
        this.horaFim = LocalDateTime.now();
        if (observacoes != null && !observacoes.isBlank()) {
            validarObservacoes(observacoes);
            this.observacoesGerais = observacoes;
        }
    }

    /**
     * Cancela a sessão de treino.
     */
    public void cancelar() {
        if (this.status == StatusSessao.FINALIZADO) {
            throw new IllegalStateException("Sessão já finalizada não pode ser cancelada.");
        }
        if (this.status == StatusSessao.CANCELADO) {
            throw new IllegalStateException("Sessão já está cancelada.");
        }
        this.status = StatusSessao.CANCELADO;
        this.horaFim = LocalDateTime.now();
    }

    /**
     * Adiciona observações gerais à sessão (em progresso).
     */
    public void adicionarObservacoes(String observacoes) {
        if (this.status == StatusSessao.CANCELADO) {
            throw new IllegalStateException("Não é possível adicionar observações a sessão cancelada.");
        }
        validarObservacoes(observacoes);
        this.observacoesGerais = observacoes;
    }

    public boolean estaEmProgresso() {
        return StatusSessao.EM_PROGRESSO.equals(this.status);
    }

    // === Validações de domínio ===

    private void validarObservacoes(String observacoes) {
        if (observacoes != null && observacoes.length() > 2000) {
            throw new IllegalArgumentException("Observações não podem exceder 2000 caracteres.");
        }
    }

    // === Getters e Setters ===

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getUsuarioId() { return usuarioId; }
    public void setUsuarioId(UUID usuarioId) { this.usuarioId = usuarioId; }

    public UUID getVersaoFichaTreinoId() { return versaoFichaTreinoId; }
    public void setVersaoFichaTreinoId(UUID versaoFichaTreinoId) { this.versaoFichaTreinoId = versaoFichaTreinoId; }

    public VersaoFichaTreino getVersaoFichaTreino() { return versaoFichaTreino; }
    public void setVersaoFichaTreino(VersaoFichaTreino versaoFichaTreino) {
        this.versaoFichaTreino = versaoFichaTreino;
        if (versaoFichaTreino != null) {
            this.versaoFichaTreinoId = versaoFichaTreino.getId();
        }
    }

    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }

    public LocalDateTime getHoraInicio() { return horaInicio; }
    public void setHoraInicio(LocalDateTime horaInicio) { this.horaInicio = horaInicio; }

    public LocalDateTime getHoraFim() { return horaFim; }
    public void setHoraFim(LocalDateTime horaFim) { this.horaFim = horaFim; }

    public String getObservacoesGerais() { return observacoesGerais; }
    public void setObservacoesGerais(String observacoesGerais) { this.observacoesGerais = observacoesGerais; }

    public StatusSessao getStatus() { return status; }
    public void setStatus(StatusSessao status) { this.status = status; }

    public List<ExercicioExecutado> getExercicios() { return exercicios; }
    public void setExercicios(List<ExercicioExecutado> exercicios) { this.exercicios = exercicios; }

    public Long getVersao() { return versao; }
    public void setVersao(Long versao) { this.versao = versao; }

    public enum StatusSessao {
        EM_PROGRESSO, FINALIZADO, CANCELADO
    }
}
