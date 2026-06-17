package com.gymbackend.application.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * DTO para registrar uma série executada durante treino.
 */
public class RegistrarSerieDTO {

    @NotNull(message = "Peso é obrigatório.")
    @DecimalMin(value = "0", message = "Peso não pode ser negativo.")
    @DecimalMax(value = "500", message = "Peso não pode exceder 500kg.")
    private BigDecimal peso;

    @NotNull(message = "Repetições são obrigatórias.")
    @Min(value = 0, message = "Repetições não podem ser negativas.")
    @Max(value = 100, message = "Repetições não podem exceder 100.")
    private Integer repeticoes;

    public RegistrarSerieDTO() {}

    public BigDecimal getPeso() { return peso; }
    public void setPeso(BigDecimal peso) { this.peso = peso; }

    public Integer getRepeticoes() { return repeticoes; }
    public void setRepeticoes(Integer repeticoes) { this.repeticoes = repeticoes; }
}
