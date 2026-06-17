package com.gymbackend.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO para atualização de exercício.
 */
public class AtualizarExercicioDTO {

    @NotBlank(message = "Nome é obrigatório.")
    @Size(max = 255, message = "Nome não pode exceder 255 caracteres.")
    private String nome;

    @NotBlank(message = "Descrição é obrigatória.")
    @Size(max = 1000, message = "Descrição não pode exceder 1000 caracteres.")
    private String descricao;

    public AtualizarExercicioDTO() {}

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
}
