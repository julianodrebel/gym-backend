package com.gymbackend.application.dto;

import com.gymbackend.domain.entities.FichaTreino;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO de resposta para Ficha de Treino.
 */
public class FichaDTO {

    private UUID id;
    private String nome;
    private UUID versaoAtivaId;
    private VersaoFichaDTO versaoAtiva;
    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;

    public FichaDTO() {}

    public static FichaDTO from(FichaTreino ficha) {
        FichaDTO dto = new FichaDTO();
        dto.setId(ficha.getId());
        dto.setNome(ficha.getNome());
        dto.setVersaoAtivaId(ficha.getVersaoAtivaId());
        dto.setCriadoEm(ficha.getCriadoEm());
        dto.setAtualizadoEm(ficha.getAtualizadoEm());

        if (ficha.getVersaoAtiva() != null) {
            dto.setVersaoAtiva(VersaoFichaDTO.from(ficha.getVersaoAtiva(), true));
        }
        return dto;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public UUID getVersaoAtivaId() { return versaoAtivaId; }
    public void setVersaoAtivaId(UUID versaoAtivaId) { this.versaoAtivaId = versaoAtivaId; }

    public VersaoFichaDTO getVersaoAtiva() { return versaoAtiva; }
    public void setVersaoAtiva(VersaoFichaDTO versaoAtiva) { this.versaoAtiva = versaoAtiva; }

    public LocalDateTime getCriadoEm() { return criadoEm; }
    public void setCriadoEm(LocalDateTime criadoEm) { this.criadoEm = criadoEm; }

    public LocalDateTime getAtualizadoEm() { return atualizadoEm; }
    public void setAtualizadoEm(LocalDateTime atualizadoEm) { this.atualizadoEm = atualizadoEm; }
}
