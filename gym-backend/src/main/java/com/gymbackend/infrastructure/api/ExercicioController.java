package com.gymbackend.infrastructure.api;

import com.gymbackend.application.dto.AtualizarExercicioDTO;
import com.gymbackend.application.dto.CriarExercicioDTO;
import com.gymbackend.application.dto.ExercicioDTO;
import com.gymbackend.application.dto.PaginaDTO;
import com.gymbackend.domain.entities.Exercicio;
import com.gymbackend.domain.exceptions.EntidadeNaoEncontradaException;
import com.gymbackend.domain.repositories.ExercicioRepository;
import com.gymbackend.domain.usecases.AtualizarExercicioUseCase;
import com.gymbackend.domain.usecases.CriarExercicioUseCase;
import com.gymbackend.domain.usecases.InativarExercicioUseCase;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;

import java.net.URI;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Controller REST para gerenciamento de Exercícios.
 * Endpoints: POST, GET, PUT, DELETE /api/exercicios
 */
@Path("/api/exercicios")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ExercicioController {

    private static final Logger LOG = Logger.getLogger(ExercicioController.class);
    private static final int TAMANHO_PAGINA_PADRAO = 50;
    private static final int TAMANHO_PAGINA_MAXIMO = 100;

    @Inject
    CriarExercicioUseCase criarExercicioUseCase;

    @Inject
    InativarExercicioUseCase inativarExercicioUseCase;

    @Inject
    AtualizarExercicioUseCase atualizarExercicioUseCase;

    @Inject
    ExercicioRepository exercicioRepository;

    /**
     * POST /api/exercicios - Criar exercício
     */
    @POST
    @RolesAllowed("usuario")
    public Response criar(@Valid CriarExercicioDTO dto) {
        LOG.debugf("POST /api/exercicios - nome=%s", dto.getNome());

        Exercicio exercicio = criarExercicioUseCase.executar(
                dto.getNome(), dto.getDescricao(), dto.getGrupoMuscular());

        ExercicioDTO resposta = ExercicioDTO.from(exercicio);
        return Response.created(URI.create("/api/exercicios/" + exercicio.getId()))
                .entity(resposta)
                .build();
    }

    /**
     * GET /api/exercicios/{id} - Buscar exercício por ID
     */
    @GET
    @Path("/{id}")
    @RolesAllowed("usuario")
    public Response buscarPorId(@PathParam("id") UUID id) {
        LOG.debugf("GET /api/exercicios/%s", id);

        Exercicio exercicio = exercicioRepository.buscarPorId(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Exercício não encontrado: " + id));

        return Response.ok(ExercicioDTO.from(exercicio)).build();
    }

    /**
     * GET /api/exercicios - Listar exercícios com paginação
     */
    @GET
    @RolesAllowed("usuario")
    public Response listar(
            @QueryParam("pagina") @DefaultValue("0") int pagina,
            @QueryParam("tamanho") @DefaultValue("50") int tamanho,
            @QueryParam("apenasAtivos") @DefaultValue("true") boolean apenasAtivos) {

        LOG.debugf("GET /api/exercicios - pagina=%d, tamanho=%d", pagina, tamanho);

        int tamanhoPagina = Math.min(tamanho, TAMANHO_PAGINA_MAXIMO);

        List<Exercicio> exercicios;
        long total;

        if (apenasAtivos) {
            exercicios = exercicioRepository.listarAtivos(pagina, tamanhoPagina);
            total = exercicioRepository.contarAtivos();
        } else {
            exercicios = exercicioRepository.listarTodos(pagina, tamanhoPagina);
            total = exercicioRepository.contarTodos();
        }

        List<ExercicioDTO> dtos = exercicios.stream()
                .map(ExercicioDTO::from)
                .collect(Collectors.toList());

        PaginaDTO<ExercicioDTO> pagina_ = new PaginaDTO<>(dtos, total, pagina, tamanhoPagina);
        return Response.ok(pagina_).build();
    }

    /**
     * PUT /api/exercicios/{id} - Atualizar exercício
     */
    @PUT
    @Path("/{id}")
    @RolesAllowed("usuario")
    public Response atualizar(@PathParam("id") UUID id, @Valid AtualizarExercicioDTO dto) {
        LOG.debugf("PUT /api/exercicios/%s", id);

        Exercicio atualizado = atualizarExercicioUseCase.executar(id, dto.getNome(), dto.getDescricao());
        return Response.ok(ExercicioDTO.from(atualizado)).build();
    }

    /**
     * DELETE /api/exercicios/{id} - Inativar exercício (soft delete)
     */
    @DELETE
    @Path("/{id}")
    @RolesAllowed("usuario")
    public Response inativar(@PathParam("id") UUID id) {
        LOG.debugf("DELETE /api/exercicios/%s", id);

        inativarExercicioUseCase.executar(id);
        return Response.noContent().build();
    }

    /**
     * POST /api/exercicios/{id}/reativar - Reativar exercício inativado
     */
    @POST
    @Path("/{id}/reativar")
    @RolesAllowed("usuario")
    public Response reativar(@PathParam("id") UUID id) {
        LOG.debugf("POST /api/exercicios/%s/reativar", id);

        Exercicio exercicio = exercicioRepository.buscarPorId(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Exercício não encontrado: " + id));
        exercicio.reativar();
        exercicioRepository.salvar(exercicio);
        return Response.ok(ExercicioDTO.from(exercicio)).build();
    }
}
