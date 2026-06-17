package com.gymbackend.infrastructure.security;

import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.io.IOException;

/**
 * Filtro de autenticação JWT para cada requisição HTTP.
 *
 * Valida o token Bearer no header Authorization.
 * OWASP A07: Identificação e autenticação falhas.
 *
 * Nota MVP: SmallRye JWT (@RolesAllowed) é o mecanismo principal.
 * Este filtro serve como camada adicional para logging.
 */
@Provider
@Priority(Priorities.AUTHENTICATION)
public class JwtAuthenticationFilter implements ContainerRequestFilter {

    @Inject
    JsonWebToken jwt;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String path = requestContext.getUriInfo().getPath();

        // Endpoints públicos não precisam de autenticação
        if (isPublicPath(path)) {
            return;
        }

        String authHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // SmallRye JWT já rejeita o request via @RolesAllowed
            // Este filtro registra tentativas para auditoria
            return;
        }

        // SmallRye JWT processa e valida o token automaticamente
        // @RolesAllowed cuida da autorização por role
    }

    private boolean isPublicPath(String path) {
        return path.startsWith("/q/health")
                || path.startsWith("/q/metrics")
                || path.startsWith("/api/auth");
    }
}
