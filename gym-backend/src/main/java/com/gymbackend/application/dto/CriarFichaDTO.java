package com.gymbackend.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO para criação de ficha de treino.
 */
public class CriarFichaDTO {

    @NotBlank(message = "Nome da ficha é obrigatório.")
    @Size(max = 255, message = "Nome não pode exceder 255 caracteres.")
    private String nome;

    public CriarFichaDTO() {}

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
}
