package com.gymbackend.infrastructure.api;

import com.gymbackend.application.dto.SugestaoProgressaoDTO;
import com.gymbackend.domain.services.EvolucaoService;
import com.gymbackend.domain.services.ProgressaoService;
import com.gymbackend.domain.entities.SerieExecutada;
import com.gymbackend.domain.repositories.ExercicioExecutadoRepository;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Controller REST para Histórico, Evolução e Progressão de Exercícios.
 */
@Path("/api/exercicios")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class HistoricoController {

    @Inject
    ExercicioExecutadoRepository exercicioExecRepository;

    @Inject
    ProgressaoService progressaoService;

    @Inject
    EvolucaoService evolucaoService;

    /**
     * GET /api/exercicios/{id}/historico - Histórico paginado de séries
     */
    @GET
    @Path("/{id}/historico")
    @RolesAllowed("usuario")
    public Response historico(
            @PathParam("id") UUID exercicioId,
            @QueryParam("pagina") @DefaultValue("0") int pagina,
            @QueryParam("tamanho") @DefaultValue("50") int tamanho,
            @QueryParam("dataInicio") String dataInicio,
            @QueryParam("dataFim") String dataFim) {

        LocalDateTime inicio = dataInicio != null ? LocalDateTime.parse(dataInicio + "T00:00:00") : LocalDateTime.MIN;
        LocalDateTime fim = dataFim != null ? LocalDateTime.parse(dataFim + "T23:59:59") : LocalDateTime.MAX;

        List<SerieExecutada> series = exercicioExecRepository.buscarSeriesPorExercicioEPeriodo(
                exercicioId, inicio, fim, pagina, Math.min(tamanho, 500));
        long total = exercicioExecRepository.contarSeriesPorExercicio(exercicioId);

        List<Map<String, Object>> conteudo = series.stream()
                .map(s -> Map.<String, Object>of(
                        "id", s.getId(),
                        "numeroSerie", s.getNumeroSerie(),
                        "peso", s.getPeso(),
                        "repeticoes", s.getRepeticoes(),
                        "volume", s.calcularVolume(),
                        "horarioExecucao", s.getHorarioExecucao()
                ))
                .toList();

        return Response.ok(Map.of(
                "conteudo", conteudo,
                "totalElementos", total,
                "pagina", pagina,
                "tamanho", tamanho
        )).build();
    }

    /**
     * GET /api/exercicios/{id}/evolucao - Evolução de cargas ao longo do tempo
     */
    @GET
    @Path("/{id}/evolucao")
    @RolesAllowed("usuario")
    public Response evolucao(@PathParam("id") UUID exercicioId) {
        List<SerieExecutada> series = exercicioExecRepository
                .buscarUltimasSeriesPorExercicio(exercicioId, 50);

        BigDecimal cargaMaxima = evolucaoService.calcularCargaMaxima(series);
        BigDecimal volumeTotal = evolucaoService.calcularVolumeTotal(series);
        double mediaReps = evolucaoService.calcularMediaRepeticoes(series);

        return Response.ok(Map.of(
                "exercicioId", exercicioId,
                "totalSeries", series.size(),
                "cargaMaxima", cargaMaxima,
                "volumeTotal", volumeTotal,
                "mediaRepeticoes", mediaReps
        )).build();
    }

    /**
     * GET /api/exercicios/{id}/ultima-execucao - Última execução do exercício
     */
    @GET
    @Path("/{id}/ultima-execucao")
    @RolesAllowed("usuario")
    public Response ultimaExecucao(@PathParam("id") UUID exercicioId) {
        return exercicioExecRepository.buscarUltimaExecucaoPorExercicio(exercicioId)
                .map(exec -> {
                    List<SerieExecutada> series = exercicioExecRepository
                            .listarSeriesPorExercicioExecutado(exec.getId());
                    BigDecimal cargaMax = evolucaoService.calcularCargaMaxima(series);

                    return Response.ok(Map.of(
                            "exercicioId", exercicioId,
                            "cargaMaxima", cargaMax,
                            "numeroSeries", series.size(),
                            "observacoes", exec.getObservacoes() != null ? exec.getObservacoes() : ""
                    )).build();
                })
                .orElse(Response.ok(Map.of("mensagem", "Sem histórico de execuções.")).build());
    }

    /**
     * GET /api/exercicios/{id}/melhor-carga - Melhor carga registrada
     */
    @GET
    @Path("/{id}/melhor-carga")
    @RolesAllowed("usuario")
    public Response melhorCarga(@PathParam("id") UUID exercicioId) {
        return exercicioExecRepository.buscarMelhorCargaPorExercicio(exercicioId)
                .map(serie -> Response.ok(Map.of(
                        "cargaMaxima", serie.getPeso(),
                        "repeticoes", serie.getRepeticoes(),
                        "data", serie.getHorarioExecucao().toLocalDate()
                )).build())
                .orElse(Response.ok(Map.of("mensagem", "Sem histórico de cargas.")).build());
    }

    /**
     * GET /api/exercicios/{id}/progressao - Sugestão de progressão automática
     */
    @GET
    @Path("/{id}/progressao")
    @RolesAllowed("usuario")
    public Response progressao(@PathParam("id") UUID exercicioId) {
        ProgressaoService.SugestaoProgressao sugestao = progressaoService.calcular(exercicioId);
        return Response.ok(SugestaoProgressaoDTO.from(sugestao)).build();
    }
}
