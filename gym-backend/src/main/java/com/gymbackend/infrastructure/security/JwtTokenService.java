package com.gymbackend.infrastructure.security;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.keys.HmacKey;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Serviço de geração e validação de tokens JWT.
 *
 * Responsabilidades:
 * - Gerar access tokens (15 minutos)
 * - Gerar refresh tokens (7 dias)
 * - Validar tokens recebidos
 *
 * Segurança (OWASP A02):
 * - Tokens com expiração
 * - Assinatura HMAC-SHA256
 * - Claims de usuário isolados
 */
@ApplicationScoped
public class JwtTokenService {

    private static final String CLAIM_USUARIO_ID = "usuarioId";
    private static final String CLAIM_ROLES = "groups";
    private static final String ISSUER = "gym-backend";
    private static final int MINUTOS_ACCESS_TOKEN = 15;
    private static final int DIAS_REFRESH_TOKEN = 7;

    @ConfigProperty(name = "jwt.secret", defaultValue = "gym-backend-secret-key-needs-32-bytes!!")
    String jwtSecret;

    /**
     * Gera um access token JWT de curta duração (15 min).
     */
    public String gerarAccessToken(UUID usuarioId, List<String> roles) {
        try {
            JwtClaims claims = new JwtClaims();
            claims.setIssuer(ISSUER);
            claims.setSubject(usuarioId.toString());
            claims.setIssuedAtToNow();
            claims.setExpirationTimeMinutesInTheFuture(MINUTOS_ACCESS_TOKEN);
            claims.setGeneratedJwtId();
            claims.setClaim(CLAIM_USUARIO_ID, usuarioId.toString());
            claims.setStringListClaim(CLAIM_ROLES, roles);

            JsonWebSignature jws = new JsonWebSignature();
            jws.setPayload(claims.toJson());
            jws.setKey(buildKey());
            jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.HMAC_SHA256);

            return jws.getCompactSerialization();
        } catch (Exception e) {
            throw new RuntimeException("Falha ao gerar access token", e);
        }
    }

    /**
     * Gera um refresh token JWT de longa duração (7 dias).
     */
    public String gerarRefreshToken(UUID usuarioId) {
        try {
            JwtClaims claims = new JwtClaims();
            claims.setIssuer(ISSUER);
            claims.setSubject(usuarioId.toString());
            claims.setIssuedAtToNow();
            claims.setExpirationTimeMinutesInTheFuture(DIAS_REFRESH_TOKEN * 24 * 60L);
            claims.setGeneratedJwtId();
            claims.setClaim("tipo", "refresh");
            claims.setClaim(CLAIM_USUARIO_ID, usuarioId.toString());

            JsonWebSignature jws = new JsonWebSignature();
            jws.setPayload(claims.toJson());
            jws.setKey(buildKey());
            jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.HMAC_SHA256);

            return jws.getCompactSerialization();
        } catch (Exception e) {
            throw new RuntimeException("Falha ao gerar refresh token", e);
        }
    }

    /**
     * Valida e extrai claims de um token JWT.
     */
    public Map<String, Object> validarToken(String token) {
        try {
            JwtConsumer consumer = new JwtConsumerBuilder()
                    .setRequireExpirationTime()
                    .setAllowedClockSkewInSeconds(30)
                    .setRequireSubject()
                    .setExpectedIssuer(ISSUER)
                    .setVerificationKey(buildKey())
                    .build();

            JwtClaims claims = consumer.processToClaims(token);
            return claims.getClaimsMap();
        } catch (Exception e) {
            // OWASP A07: Não expor detalhes internos de falha de autenticação
            throw new SecurityException("Token inválido ou expirado");
        }
    }

    /**
     * Extrai o ID do usuário de um token sem validar (use somente após validar).
     */
    public UUID extrairUsuarioId(String token) {
        Map<String, Object> claims = validarToken(token);
        return UUID.fromString((String) claims.get(CLAIM_USUARIO_ID));
    }

    private Key buildKey() {
        byte[] bytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        if (bytes.length < 32) {
            throw new IllegalStateException("jwt.secret deve ter no mínimo 32 bytes");
        }
        return new HmacKey(bytes);
    }
}
