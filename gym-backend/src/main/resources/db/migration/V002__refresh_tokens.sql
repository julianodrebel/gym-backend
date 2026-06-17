-- ==========================================================================
-- V002 - Chave de Segurança: Criação de tabela de refresh tokens
-- Data: 2026-06-17
-- Descrição: Suporte a JWT refresh tokens
-- ==========================================================================

CREATE TABLE refresh_tokens (
    id          UUID        NOT NULL DEFAULT uuid_generate_v4(),
    usuario_id  UUID        NOT NULL,
    token_hash  VARCHAR(512) NOT NULL,
    expira_em   TIMESTAMP   NOT NULL,
    criado_em   TIMESTAMP   NOT NULL DEFAULT NOW(),
    revogado    BOOLEAN     NOT NULL DEFAULT FALSE,

    CONSTRAINT pk_refresh_tokens PRIMARY KEY (id)
);

CREATE INDEX idx_refresh_token_hash ON refresh_tokens (token_hash);
CREATE INDEX idx_refresh_usuario ON refresh_tokens (usuario_id);
