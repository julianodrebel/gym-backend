package com.gymbackend.infrastructure.security;

import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Middleware de Rate Limiting para endpoints sensíveis.
 *
 * Implementa janela deslizante de 1 minuto por IP.
 * OWASP A07: Proteção contra força bruta.
 *
 * Limites:
 * - /api/auth: 10 requisições/minuto por IP
 * - Outros endpoints: sem limite via este filtro
 */
@Provider
@Priority(Priorities.AUTHORIZATION - 100)
@ApplicationScoped
public class RateLimitingFilter implements ContainerRequestFilter {

    private static final int LIMITE_AUTH = 10;
    private static final int JANELA_SEGUNDOS = 60;

    private final Map<String, RateLimitEntry> contadores = new ConcurrentHashMap<>();

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String path = requestContext.getUriInfo().getPath();

        if (!path.startsWith("/api/auth")) {
            return;
        }

        String clientIp = obterIpCliente(requestContext);
        String chave = clientIp + ":" + path;

        RateLimitEntry entry = contadores.computeIfAbsent(chave, k -> new RateLimitEntry());

        if (entry.excedeuLimite(LIMITE_AUTH, JANELA_SEGUNDOS)) {
            requestContext.abortWith(
                Response.status(429)
                    .entity(Map.of(
                        "codigo", "RATE_LIMIT_EXCEDIDO",
                        "mensagem", "Muitas tentativas. Aguarde " + JANELA_SEGUNDOS + " segundos."
                    ))
                    .build()
            );
        }
    }

    private String obterIpCliente(ContainerRequestContext ctx) {
        // OWASP A03: Não confiar em headers X-Forwarded-For sem validação em produção
        String forwarded = ctx.getHeaderString("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return "unknown";
    }

    private static class RateLimitEntry {
        private final AtomicInteger contador = new AtomicInteger(0);
        private volatile Instant inicioJanela = Instant.now();

        boolean excedeuLimite(int limite, int janelaSeg) {
            Instant agora = Instant.now();
            long segundosDecorridos = agora.getEpochSecond() - inicioJanela.getEpochSecond();

            if (segundosDecorridos >= janelaSeg) {
                inicioJanela = agora;
                contador.set(1);
                return false;
            }

            int atual = contador.incrementAndGet();
            return atual > limite;
        }
    }
}
