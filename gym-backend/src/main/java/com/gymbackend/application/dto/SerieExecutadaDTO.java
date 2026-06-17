package com.gymbackend.application.dto;

import com.gymbackend.domain.entities.SerieExecutada;
import com.gymbackend.domain.services.TempoDescansoService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO de resposta para série executada (inclui metadados do cronômetro).
 */
public class SerieExecutadaDTO {

    private UUID id;
    private Integer numeroSerie;
    private BigDecimal peso;
    private Integer repeticoes;
    private BigDecimal volume;
    private LocalDateTime horarioExecucao;
    private CronoAvisoCronometroDTO cronoAvisoCronometro;

    public SerieExecutadaDTO() {}

    public static SerieExecutadaDTO from(SerieExecutada serie, TempoDescansoService.CronoMetadata crono) {
        SerieExecutadaDTO dto = new SerieExecutadaDTO();
        dto.setId(serie.getId());
        dto.setNumeroSerie(serie.getNumeroSerie());
        dto.setPeso(serie.getPeso());
        dto.setRepeticoes(serie.getRepeticoes());
        dto.setVolume(serie.calcularVolume());
        dto.setHorarioExecucao(serie.getHorarioExecucao());

        if (crono != null) {
            dto.setCronoAvisoCronometro(new CronoAvisoCronometroDTO(
                    crono.getTempoDescansoRecomendado(),
                    crono.isIniciarCrono(),
                    crono.isAvisoIntervaloInsuficiente()
            ));
        }
        return dto;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public Integer getNumeroSerie() { return numeroSerie; }
    public void setNumeroSerie(Integer numeroSerie) { this.numeroSerie = numeroSerie; }

    public BigDecimal getPeso() { return peso; }
    public void setPeso(BigDecimal peso) { this.peso = peso; }

    public Integer getRepeticoes() { return repeticoes; }
    public void setRepeticoes(Integer repeticoes) { this.repeticoes = repeticoes; }

    public BigDecimal getVolume() { return volume; }
    public void setVolume(BigDecimal volume) { this.volume = volume; }

    public LocalDateTime getHorarioExecucao() { return horarioExecucao; }
    public void setHorarioExecucao(LocalDateTime horarioExecucao) { this.horarioExecucao = horarioExecucao; }

    public CronoAvisoCronometroDTO getCronoAvisoCronometro() { return cronoAvisoCronometro; }
    public void setCronoAvisoCronometro(CronoAvisoCronometroDTO cronoAvisoCronometro) { this.cronoAvisoCronometro = cronoAvisoCronometro; }
}
