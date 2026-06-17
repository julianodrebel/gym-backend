package com.gymbackend.infrastructure.api;

import com.gymbackend.application.dto.CriarFichaDTO;
import com.gymbackend.application.dto.FichaDTO;
import com.gymbackend.application.dto.PaginaDTO;
import com.gymbackend.application.dto.VersaoFichaDTO;
import com.gymbackend.domain.entities.FichaTreino;
import com.gymbackend.domain.entities.VersaoFichaTreino;
import com.gymbackend.domain.exceptions.EntidadeNaoEncontradaException;
import com.gymbackend.domain.repositories.FichaTreinoRepository;
import com.gymbackend.domain.repositories.VersaoFichaTreinoRepository;
import com.gymbackend.domain.usecases.CriarFichaUseCase;
import com.gymbackend.domain.usecases.CriarNovaVersaoUseCase;
import com.gymbackend.domain.usecases.DuplicarVersaoUseCase;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import org.jboss.logging.Logger;

import java.net.URI;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Controller REST para gerenciamento de Fichas de Treino.
 * Endpoints: POST, GET /api/fichas e /api/fichas/{id}/versoes
 */
@Path("/api/fichas")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class FichaTreinoController {

    private static final Logger LOG = Logger.getLogger(FichaTreinoController.class);
    private static final UUID USUARIO_PADRAO = UUID.fromString("00000000-0000-0000-0000-000000000001");

    @Inject
    CriarFichaUseCase criarFichaUseCase;

    @Inject
    CriarNovaVersaoUseCase criarNovaVersaoUseCase;

    @Inject
    DuplicarVersaoUseCase duplicarVersaoUseCase;

    @Inject
    FichaTreinoRepository fichaRepository;

    @Inject
    VersaoFichaTreinoRepository versaoRepository;

    /**
     * POST /api/fichas - Criar ficha de treino (com V1 automática)
     */
    @POST
    @RolesAllowed("usuario")
    public Response criar(@Valid CriarFichaDTO dto) {
        LOG.debugf("POST /api/fichas - nome=%s", dto.getNome());

        FichaTreino ficha = criarFichaUseCase.executar(USUARIO_PADRAO, dto.getNome());
        return Response.created(URI.create("/api/fichas/" + ficha.getId()))
                .entity(FichaDTO.from(ficha))
                .build();
    }

    /**
     * GET /api/fichas/{id} - Buscar ficha por ID
     */
    @GET
    @Path("/{id}")
    @RolesAllowed("usuario")
    public Response buscarPorId(@PathParam("id") UUID id) {
        FichaTreino ficha = fichaRepository.buscarPorId(id)
                .orElseThrow(() -> EntidadeNaoEncontradaException.fichaTreino(id));
        return Response.ok(FichaDTO.from(ficha)).build();
    }

    /**
     * GET /api/fichas - Listar fichas do usuário
     */
    @GET
    @RolesAllowed("usuario")
    public Response listar(
            @QueryParam("pagina") @DefaultValue("0") int pagina,
            @QueryParam("tamanho") @DefaultValue("20") int tamanho) {

        List<FichaTreino> fichas = fichaRepository.listarPorUsuario(USUARIO_PADRAO, pagina, tamanho);
        long total = fichaRepository.contarPorUsuario(USUARIO_PADRAO);

        List<FichaDTO> dtos = fichas.stream().map(FichaDTO::from).collect(Collectors.toList());
        return Response.ok(new PaginaDTO<>(dtos, total, pagina, tamanho)).build();
    }

    /**
     * GET /api/fichas/{id}/versoes - Listar versões da ficha
     */
    @GET
    @Path("/{id}/versoes")
    @RolesAllowed("usuario")
    public Response listarVersoes(@PathParam("id") UUID fichaId) {
        FichaTreino ficha = fichaRepository.buscarPorId(fichaId)
                .orElseThrow(() -> EntidadeNaoEncontradaException.fichaTreino(fichaId));

        List<VersaoFichaTreino> versoes = versaoRepository.listarPorFicha(fichaId);
        List<VersaoFichaDTO> dtos = versoes.stream()
                .map(v -> VersaoFichaDTO.from(v, v.getId().equals(ficha.getVersaoAtivaId())))
                .collect(Collectors.toList());

        return Response.ok(dtos).build();
    }

    /**
     * POST /api/fichas/{id}/versoes - Criar nova versão
     */
    @POST
    @Path("/{id}/versoes")
    @RolesAllowed("usuario")
    public Response criarNovaVersao(@PathParam("id") UUID fichaId,
                                    List<VersaoFichaDTO.ExercicioOrdemDTO> exerciciosDTO) {
        LOG.debugf("POST /api/fichas/%s/versoes", fichaId);

        List<VersaoFichaTreino.ExercicioOrdem> exercicios = null;
        if (exerciciosDTO != null) {
            exercicios = exerciciosDTO.stream()
                    .map(e -> new VersaoFichaTreino.ExercicioOrdem(e.getExercicioId(), e.getOrdem()))
                    .collect(Collectors.toList());
        }

        VersaoFichaTreino novaVersao = criarNovaVersaoUseCase.executar(fichaId, exercicios);
        FichaTreino ficha = fichaRepository.buscarPorId(fichaId)
                .orElseThrow(() -> EntidadeNaoEncontradaException.fichaTreino(fichaId));

        return Response.created(URI.create("/api/fichas/" + fichaId + "/versoes/" + novaVersao.getNumero()))
                .entity(VersaoFichaDTO.from(novaVersao, novaVersao.getId().equals(ficha.getVersaoAtivaId())))
                .build();
    }

    /**
     * POST /api/fichas/{id}/versoes/{numero}/ativar - Ativar versão específica
     */
    @POST
    @Path("/{id}/versoes/{numero}/ativar")
    @RolesAllowed("usuario")
    public Response ativarVersao(@PathParam("id") UUID fichaId,
                                  @PathParam("numero") Integer numero) {
        LOG.debugf("POST /api/fichas/%s/versoes/%d/ativar", fichaId, numero);

        FichaTreino ficha = fichaRepository.buscarPorId(fichaId)
                .orElseThrow(() -> EntidadeNaoEncontradaException.fichaTreino(fichaId));

        VersaoFichaTreino versao = versaoRepository.buscarPorFichaENumero(fichaId, numero)
                .orElseThrow(() -> new EntidadeNaoEncontradaException(
                        "Versão V" + numero + " não encontrada para ficha " + fichaId));

        ficha.ativarVersao(versao);
        fichaRepository.salvar(ficha);

        return Response.ok(VersaoFichaDTO.from(versao, true)).build();
    }

    /**
     * POST /api/fichas/{id}/versoes/{numero}/duplicar - Duplicar versão
     */
    @POST
    @Path("/{id}/versoes/{numero}/duplicar")
    @RolesAllowed("usuario")
    public Response duplicarVersao(@PathParam("id") UUID fichaId,
                                    @PathParam("numero") Integer numero) {
        LOG.debugf("POST /api/fichas/%s/versoes/%d/duplicar", fichaId, numero);

        VersaoFichaTreino versaoOrigem = versaoRepository.buscarPorFichaENumero(fichaId, numero)
                .orElseThrow(() -> new EntidadeNaoEncontradaException(
                        "Versão V" + numero + " não encontrada"));

        VersaoFichaTreino copia = duplicarVersaoUseCase.executar(fichaId, versaoOrigem.getId());
        return Response.ok(VersaoFichaDTO.from(copia, false)).build();
    }
}
