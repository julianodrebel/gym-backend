package com.gymbackend.application.dto;

import com.gymbackend.domain.entities.Exercicio;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO de resposta para Exercício.
 */
public class ExercicioDTO {

    private UUID id;
    private String nome;
    private String descricao;
    private String grupoMuscular;
    private String status;
    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;

    public ExercicioDTO() {}

    public static ExercicioDTO from(Exercicio exercicio) {
        ExercicioDTO dto = new ExercicioDTO();
        dto.setId(exercicio.getId());
        dto.setNome(exercicio.getNome());
        dto.setDescricao(exercicio.getDescricao());
        dto.setGrupoMuscular(exercicio.getGrupoMuscular());
        dto.setStatus(exercicio.getStatus().name());
        dto.setCriadoEm(exercicio.getCriadoEm());
        dto.setAtualizadoEm(exercicio.getAtualizadoEm());
        return dto;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public String getGrupoMuscular() { return grupoMuscular; }
    public void setGrupoMuscular(String grupoMuscular) { this.grupoMuscular = grupoMuscular; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCriadoEm() { return criadoEm; }
    public void setCriadoEm(LocalDateTime criadoEm) { this.criadoEm = criadoEm; }

    public LocalDateTime getAtualizadoEm() { return atualizadoEm; }
    public void setAtualizadoEm(LocalDateTime atualizadoEm) { this.atualizadoEm = atualizadoEm; }
}
