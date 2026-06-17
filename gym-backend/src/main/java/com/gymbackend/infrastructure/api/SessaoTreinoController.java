package com.gymbackend.infrastructure.api;

import com.gymbackend.application.dto.RegistrarSerieDTO;
import com.gymbackend.application.dto.SerieExecutadaDTO;
import com.gymbackend.domain.entities.ExercicioExecutado;
import com.gymbackend.domain.entities.SerieExecutada;
import com.gymbackend.domain.entities.SessaoTreino;
import com.gymbackend.domain.exceptions.EntidadeNaoEncontradaException;
import com.gymbackend.domain.repositories.ExercicioExecutadoRepository;
import com.gymbackend.domain.repositories.SessaoTreinoRepository;
import com.gymbackend.domain.services.TempoDescansoService;
import com.gymbackend.domain.usecases.SessaoTreinoUseCases;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;

import java.net.URI;
import java.util.Map;
import java.util.UUID;

/**
 * Controller REST para execução de Sessões de Treino.
 */
@Path("/api/sessoes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SessaoTreinoController {

    private static final Logger LOG = Logger.getLogger(SessaoTreinoController.class);
    private static final UUID USUARIO_PADRAO = UUID.fromString("00000000-0000-0000-0000-000000000001");

    @Inject
    SessaoTreinoUseCases sessaoUseCases;

    @Inject
    SessaoTreinoRepository sessaoRepository;

    @Inject
    ExercicioExecutadoRepository exercicioExecRepository;

    @Inject
    TempoDescansoService tempoDescansoService;

    /**
     * POST /api/sessoes - Iniciar nova sessão de treino
     */
    @POST
    @RolesAllowed("usuario")
    public Response iniciar(@QueryParam("fichaId") UUID fichaId) {
        LOG.debugf("POST /api/sessoes - fichaId=%s", fichaId);

        if (fichaId == null) {
            throw new IllegalArgumentException("fichaId é obrigatório.");
        }

        SessaoTreino sessao = sessaoUseCases.iniciarSessao(USUARIO_PADRAO, fichaId);
        return Response.created(URI.create("/api/sessoes/" + sessao.getId()))
                .entity(Map.of(
                        "id", sessao.getId(),
                        "status", sessao.getStatus(),
                        "versaoFichaTreinoId", sessao.getVersaoFichaTreinoId(),
                        "horaInicio", sessao.getHoraInicio()
                ))
                .build();
    }

    /**
     * GET /api/sessoes/{id} - Buscar sessão por ID
     */
    @GET
    @Path("/{id}")
    @RolesAllowed("usuario")
    public Response buscarPorId(@PathParam("id") UUID id) {
        SessaoTreino sessao = sessaoRepository.buscarPorId(id)
                .orElseThrow(() -> EntidadeNaoEncontradaException.sessaoTreino(id));

        return Response.ok(Map.of(
                "id", sessao.getId(),
                "status", sessao.getStatus(),
                "versaoFichaTreinoId", sessao.getVersaoFichaTreinoId(),
                "data", sessao.getData(),
                "horaInicio", sessao.getHoraInicio()
        )).build();
    }

    /**
     * POST /api/sessoes/{id}/series - Registrar série com metadados de cronômetro
     */
    @POST
    @Path("/{id}/series")
    @RolesAllowed("usuario")
    public Response registrarSerie(
            @PathParam("id") UUID sessaoId,
            @QueryParam("exercicioExecutadoId") UUID exercicioExecutadoId,
            @Valid RegistrarSerieDTO dto) {
        LOG.debugf("POST /api/sessoes/%s/series - execId=%s", sessaoId, exercicioExecutadoId);

        SerieExecutada serie = sessaoUseCases.registrarSerie(
                sessaoId, exercicioExecutadoId, dto.getPeso(), dto.getRepeticoes());

        // Calcular metadados do cronômetro (US6)
        ExercicioExecutado exec = exercicioExecRepository.buscarPorId(exercicioExecutadoId)
                .orElse(null);
        SerieExecutada penultima = null;
        if (exec != null && exec.getSeries().size() > 1) {
            penultima = exec.getSeries().get(exec.getSeries().size() - 2);
        }

        TempoDescansoService.CronoMetadata crono = tempoDescansoService.calcularMetadata(penultima);
        SerieExecutadaDTO resposta = SerieExecutadaDTO.from(serie, crono);

        return Response.created(URI.create("/api/sessoes/" + sessaoId + "/series/" + serie.getId()))
                .entity(resposta)
                .build();
    }

    /**
     * PUT /api/sessoes/{id}/observacoes - Adicionar observações gerais
     */
    @PUT
    @Path("/{id}/observacoes")
    @RolesAllowed("usuario")
    public Response adicionarObservacoes(@PathParam("id") UUID sessaoId,
                                          Map<String, String> body) {
        SessaoTreino sessao = sessaoRepository.buscarPorId(sessaoId)
                .orElseThrow(() -> EntidadeNaoEncontradaException.sessaoTreino(sessaoId));

        String obs = body.get("observacoes");
        sessao.adicionarObservacoes(obs);
        sessaoRepository.salvar(sessao);

        return Response.ok(Map.of("mensagem", "Observações adicionadas.")).build();
    }

    /**
     * PUT /api/sessoes/{id}/finalizar - Finalizar sessão
     */
    @PUT
    @Path("/{id}/finalizar")
    @RolesAllowed("usuario")
    public Response finalizar(@PathParam("id") UUID sessaoId,
                               Map<String, String> body) {
        LOG.debugf("PUT /api/sessoes/%s/finalizar", sessaoId);

        String obs = body != null ? body.get("observacoesGerais") : null;
        SessaoTreino sessao = sessaoUseCases.finalizarSessao(sessaoId, obs);

        return Response.ok(Map.of(
                "status", sessao.getStatus(),
                "horaFim", sessao.getHoraFim()
        )).build();
    }

    /**
     * PUT /api/sessoes/{id}/cancelar - Cancelar sessão
     */
    @PUT
    @Path("/{id}/cancelar")
    @RolesAllowed("usuario")
    public Response cancelar(@PathParam("id") UUID sessaoId) {
        LOG.debugf("PUT /api/sessoes/%s/cancelar", sessaoId);

        SessaoTreino sessao = sessaoUseCases.cancelarSessao(sessaoId);
        return Response.ok(Map.of(
                "status", sessao.getStatus(),
                "horaFim", sessao.getHoraFim()
        )).build();
    }
}
