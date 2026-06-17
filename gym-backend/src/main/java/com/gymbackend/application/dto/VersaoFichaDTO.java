package com.gymbackend.application.dto;

import com.gymbackend.domain.entities.VersaoFichaTreino;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * DTO de resposta para Versão de Ficha de Treino.
 */
public class VersaoFichaDTO {

    private UUID id;
    private Integer numero;
    private List<ExercicioOrdemDTO> exercicios;
    private LocalDateTime criadoEm;
    private boolean ativa;

    public VersaoFichaDTO() {}

    public static VersaoFichaDTO from(VersaoFichaTreino versao, boolean ativa) {
        VersaoFichaDTO dto = new VersaoFichaDTO();
        dto.setId(versao.getId());
        dto.setNumero(versao.getNumero());
        dto.setCriadoEm(versao.getCriadoEm());
        dto.setAtiva(ativa);

        if (versao.getExercicios() != null) {
            dto.setExercicios(versao.getExercicios().stream()
                    .map(e -> new ExercicioOrdemDTO(e.getExercicioId(), e.getOrdem()))
                    .collect(Collectors.toList()));
        }
        return dto;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public Integer getNumero() { return numero; }
    public void setNumero(Integer numero) { this.numero = numero; }

    public List<ExercicioOrdemDTO> getExercicios() { return exercicios; }
    public void setExercicios(List<ExercicioOrdemDTO> exercicios) { this.exercicios = exercicios; }

    public LocalDateTime getCriadoEm() { return criadoEm; }
    public void setCriadoEm(LocalDateTime criadoEm) { this.criadoEm = criadoEm; }

    public boolean isAtiva() { return ativa; }
    public void setAtiva(boolean ativa) { this.ativa = ativa; }

    public static class ExercicioOrdemDTO {
        private UUID exercicioId;
        private Integer ordem;

        public ExercicioOrdemDTO() {}
        public ExercicioOrdemDTO(UUID exercicioId, Integer ordem) {
            this.exercicioId = exercicioId;
            this.ordem = ordem;
        }

        public UUID getExercicioId() { return exercicioId; }
        public void setExercicioId(UUID exercicioId) { this.exercicioId = exercicioId; }
        public Integer getOrdem() { return ordem; }
        public void setOrdem(Integer ordem) { this.ordem = ordem; }
    }
}
