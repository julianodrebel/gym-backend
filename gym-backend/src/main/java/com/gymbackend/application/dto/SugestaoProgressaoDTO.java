package com.gymbackend.application.dto;

import com.gymbackend.domain.services.ProgressaoService;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * DTO de resposta para sugestão de progressão de carga.
 */
public class SugestaoProgressaoDTO {

    private String sugestao;
    private DetalhesProgressaoDTO detalhes;
    private List<SerieResumoDTO> ultimas5Series;

    public SugestaoProgressaoDTO() {}

    public static SugestaoProgressaoDTO from(ProgressaoService.SugestaoProgressao sugestao) {
        SugestaoProgressaoDTO dto = new SugestaoProgressaoDTO();
        dto.setSugestao(sugestao.getSugestao().name());

        DetalhesProgressaoDTO detalhes = new DetalhesProgressaoDTO();
        detalhes.setCargaAtual(sugestao.getCargaAtual());
        detalhes.setCargaSugerida(sugestao.getCargaSugerida());
        detalhes.setJustificativa(sugestao.getJustificativa());
        dto.setDetalhes(detalhes);

        if (sugestao.getUltimasSeries() != null) {
            dto.setUltimas5Series(sugestao.getUltimasSeries().stream()
                    .map(s -> new SerieResumoDTO(s.getPeso(), s.getRepeticoes(), s.getHorarioExecucao()))
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    public String getSugestao() { return sugestao; }
    public void setSugestao(String sugestao) { this.sugestao = sugestao; }

    public DetalhesProgressaoDTO getDetalhes() { return detalhes; }
    public void setDetalhes(DetalhesProgressaoDTO detalhes) { this.detalhes = detalhes; }

    public List<SerieResumoDTO> getUltimas5Series() { return ultimas5Series; }
    public void setUltimas5Series(List<SerieResumoDTO> ultimas5Series) { this.ultimas5Series = ultimas5Series; }

    public static class DetalhesProgressaoDTO {
        private BigDecimal cargaAtual;
        private BigDecimal cargaSugerida;
        private String justificativa;

        public BigDecimal getCargaAtual() { return cargaAtual; }
        public void setCargaAtual(BigDecimal cargaAtual) { this.cargaAtual = cargaAtual; }
        public BigDecimal getCargaSugerida() { return cargaSugerida; }
        public void setCargaSugerida(BigDecimal cargaSugerida) { this.cargaSugerida = cargaSugerida; }
        public String getJustificativa() { return justificativa; }
        public void setJustificativa(String justificativa) { this.justificativa = justificativa; }
    }

    public static class SerieResumoDTO {
        private BigDecimal peso;
        private Integer repeticoes;
        private java.time.LocalDateTime horarioExecucao;

        public SerieResumoDTO() {}
        public SerieResumoDTO(BigDecimal peso, Integer repeticoes, java.time.LocalDateTime horario) {
            this.peso = peso;
            this.repeticoes = repeticoes;
            this.horarioExecucao = horario;
        }

        public BigDecimal getPeso() { return peso; }
        public void setPeso(BigDecimal peso) { this.peso = peso; }
        public Integer getRepeticoes() { return repeticoes; }
        public void setRepeticoes(Integer repeticoes) { this.repeticoes = repeticoes; }
        public java.time.LocalDateTime getHorarioExecucao() { return horarioExecucao; }
        public void setHorarioExecucao(java.time.LocalDateTime horarioExecucao) { this.horarioExecucao = horarioExecucao; }
    }
}
