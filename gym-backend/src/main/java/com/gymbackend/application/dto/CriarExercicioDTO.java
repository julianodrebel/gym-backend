package com.gymbackend.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO para criação de exercício.
 */
public class CriarExercicioDTO {

    @NotBlank(message = "Nome é obrigatório.")
    @Size(max = 255, message = "Nome não pode exceder 255 caracteres.")
    private String nome;

    @NotBlank(message = "Descrição é obrigatória.")
    @Size(max = 1000, message = "Descrição não pode exceder 1000 caracteres.")
    private String descricao;

    @NotBlank(message = "Grupo muscular é obrigatório.")
    @Size(max = 100, message = "Grupo muscular não pode exceder 100 caracteres.")
    private String grupoMuscular;

    public CriarExercicioDTO() {}

    public CriarExercicioDTO(String nome, String descricao, String grupoMuscular) {
        this.nome = nome;
        this.descricao = descricao;
        this.grupoMuscular = grupoMuscular;
    }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public String getGrupoMuscular() { return grupoMuscular; }
    public void setGrupoMuscular(String grupoMuscular) { this.grupoMuscular = grupoMuscular; }
}
