package com.gymbackend.domain.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Entidade de domínio: Ficha de Treino.
 * Agrupa exercícios em estrutura de treino reutilizável.
 * Mantém referência para versão ativa e histórico de versões.
 */
public class FichaTreino {

    private UUID id;
    private UUID usuarioId;
    private String nome;
    private UUID versaoAtivaId;
    private VersaoFichaTreino versaoAtiva;
    private List<VersaoFichaTreino> versoes;
    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;
    private Long versao;

    public FichaTreino() {
        this.versoes = new ArrayList<>();
    }

    public FichaTreino(UUID usuarioId, String nome) {
        validarNome(nome);
        if (usuarioId == null) {
            throw new IllegalArgumentException("ID do usuário é obrigatório.");
        }
        this.id = UUID.randomUUID();
        this.usuarioId = usuarioId;
        this.nome = nome;
        this.versoes = new ArrayList<>();
        this.criadoEm = LocalDateTime.now();
        this.atualizadoEm = LocalDateTime.now();
        this.versao = 0L;
    }

    /**
     * Atualiza o nome da ficha de treino.
     */
    public void atualizarNome(String novoNome) {
        validarNome(novoNome);
        this.nome = novoNome;
        this.atualizadoEm = LocalDateTime.now();
    }

    /**
     * Define a versão ativa da ficha.
     * A versão ativa determina qual versão é usada em novos treinos.
     */
    public void ativarVersao(VersaoFichaTreino versao) {
        if (versao == null) {
            throw new IllegalArgumentException("Versão não pode ser nula.");
        }
        if (!versao.getFichaTreinoId().equals(this.id)) {
            throw new IllegalArgumentException("Versão não pertence a esta ficha.");
        }
        if (versao.estaDeletada()) {
            throw new IllegalStateException("Não é possível ativar uma versão deletada.");
        }
        this.versaoAtiva = versao;
        this.versaoAtivaId = versao.getId();
        this.atualizadoEm = LocalDateTime.now();
    }

    // === Validações de domínio ===

    private void validarNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome da ficha de treino é obrigatório.");
        }
        if (nome.length() > 255) {
            throw new IllegalArgumentException("Nome da ficha de treino não pode exceder 255 caracteres.");
        }
    }

    // === Getters e Setters ===

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getUsuarioId() { return usuarioId; }
    public void setUsuarioId(UUID usuarioId) { this.usuarioId = usuarioId; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public UUID getVersaoAtivaId() { return versaoAtivaId; }
    public void setVersaoAtivaId(UUID versaoAtivaId) { this.versaoAtivaId = versaoAtivaId; }

    public VersaoFichaTreino getVersaoAtiva() { return versaoAtiva; }
    public void setVersaoAtiva(VersaoFichaTreino versaoAtiva) {
        this.versaoAtiva = versaoAtiva;
        if (versaoAtiva != null) {
            this.versaoAtivaId = versaoAtiva.getId();
        }
    }

    public List<VersaoFichaTreino> getVersoes() { return versoes; }
    public void setVersoes(List<VersaoFichaTreino> versoes) { this.versoes = versoes; }

    public LocalDateTime getCriadoEm() { return criadoEm; }
    public void setCriadoEm(LocalDateTime criadoEm) { this.criadoEm = criadoEm; }

    public LocalDateTime getAtualizadoEm() { return atualizadoEm; }
    public void setAtualizadoEm(LocalDateTime atualizadoEm) { this.atualizadoEm = atualizadoEm; }

    public Long getVersao() { return versao; }
    public void setVersao(Long versao) { this.versao = versao; }
}
