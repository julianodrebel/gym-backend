package com.gymbackend.infrastructure.api;

import com.gymbackend.domain.exceptions.EntidadeNaoEncontradaException;
import com.gymbackend.domain.exceptions.RegraDeNegocioException;
import com.gymbackend.domain.exceptions.VersaoImutavelException;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Handler global de exceções para API REST.
 * Traduz exceções de domínio em respostas HTTP padronizadas.
 * Garante que erros não vazam informações sensíveis (OWASP).
 */
@Provider
public class GlobalExceptionHandler implements ExceptionMapper<Exception> {

    private static final Logger LOG = Logger.getLogger(GlobalExceptionHandler.class);

    @Override
    public Response toResponse(Exception exception) {

        if (exception instanceof EntidadeNaoEncontradaException) {
            return respostaErro(Response.Status.NOT_FOUND, exception.getMessage(), null);
        }

        if (exception instanceof VersaoImutavelException) {
            return respostaErro(Response.Status.BAD_REQUEST,
                    exception.getMessage(), "VERSAO_IMUTAVEL");
        }

        if (exception instanceof RegraDeNegocioException) {
            return respostaErro(Response.Status.UNPROCESSABLE_ENTITY,
                    exception.getMessage(), "REGRA_NEGOCIO");
        }

        if (exception instanceof IllegalArgumentException) {
            return respostaErro(Response.Status.BAD_REQUEST,
                    exception.getMessage(), "VALIDACAO");
        }

        if (exception instanceof IllegalStateException) {
            return respostaErro(Response.Status.CONFLICT,
                    exception.getMessage(), "ESTADO_INVALIDO");
        }

        if (exception instanceof ConstraintViolationException cve) {
            List<Map<String, String>> erros = cve.getConstraintViolations().stream()
                    .map(v -> Map.of(
                            "campo", v.getPropertyPath().toString(),
                            "mensagem", v.getMessage()
                    ))
                    .collect(Collectors.toList());

            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(Map.of(
                            "timestamp", LocalDateTime.now().toString(),
                            "status", 400,
                            "erro", "Validação falhou",
                            "codigo", "VALIDACAO",
                            "detalhes", erros
                    ))
                    .build();
        }

        // Erro genérico - não vazar detalhes (OWASP A09)
        LOG.errorf(exception, "Erro interno não esperado: %s", exception.getClass().getSimpleName());
        return respostaErro(Response.Status.INTERNAL_SERVER_ERROR,
                "Erro interno do servidor.", "ERRO_INTERNO");
    }

    private Response respostaErro(Response.Status status, String mensagem, String codigo) {
        return Response.status(status)
                .entity(Map.of(
                        "timestamp", LocalDateTime.now().toString(),
                        "status", status.getStatusCode(),
                        "erro", mensagem,
                        "codigo", codigo != null ? codigo : "ERRO"
                ))
                .build();
    }
}
