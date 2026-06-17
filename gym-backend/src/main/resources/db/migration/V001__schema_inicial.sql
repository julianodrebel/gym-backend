-- ==========================================================================
-- V001 - Schema Inicial: Acompanhamento de Treinos de Musculação
-- Data: 2026-06-17
-- Descrição: Criação de todas as tabelas do domínio
-- Entidades: exercicios, fichas_treino, versoes_ficha_treino,
--            versao_exercicio (associativa), sessoes_treino,
--            exercicios_executados, series_executadas
-- ==========================================================================

-- Extensão para UUID
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- ==========================================================================
-- TABELA: exercicios
-- Armazena exercícios de musculação reutilizáveis
-- ==========================================================================
CREATE TABLE exercicios (
    id               UUID         NOT NULL DEFAULT uuid_generate_v4(),
    nome             VARCHAR(255) NOT NULL,
    descricao        VARCHAR(1000) NOT NULL,
    grupo_muscular   VARCHAR(100) NOT NULL,
    status           VARCHAR(20)  NOT NULL DEFAULT 'ATIVO',
    criado_em        TIMESTAMP    NOT NULL DEFAULT NOW(),
    atualizado_em    TIMESTAMP    NOT NULL DEFAULT NOW(),
    deletado_em      TIMESTAMP,
    versao           BIGINT       NOT NULL DEFAULT 0,

    CONSTRAINT pk_exercicios PRIMARY KEY (id),
    CONSTRAINT ck_exercicio_status CHECK (status IN ('ATIVO', 'INATIVO'))
);

CREATE INDEX idx_exercicios_status ON exercicios (status);
CREATE INDEX idx_exercicios_grupo ON exercicios (grupo_muscular);

-- ==========================================================================
-- TABELA: fichas_treino
-- Agrupa exercícios em estrutura de treino reutilizável
-- ==========================================================================
CREATE TABLE fichas_treino (
    id               UUID         NOT NULL DEFAULT uuid_generate_v4(),
    usuario_id       UUID         NOT NULL,
    nome             VARCHAR(255) NOT NULL,
    versao_ativa_id  UUID,
    criado_em        TIMESTAMP    NOT NULL DEFAULT NOW(),
    atualizado_em    TIMESTAMP    NOT NULL DEFAULT NOW(),
    versao           BIGINT       NOT NULL DEFAULT 0,

    CONSTRAINT pk_fichas_treino PRIMARY KEY (id)
);

CREATE INDEX idx_fichas_usuario ON fichas_treino (usuario_id);

-- ==========================================================================
-- TABELA: versoes_ficha_treino
-- CRÍTICA: Versionamento IMUTÁVEL de fichas de treino
-- Cada modificação cria nova versão - versões nunca são alteradas
-- ==========================================================================
CREATE TABLE versoes_ficha_treino (
    id               UUID         NOT NULL DEFAULT uuid_generate_v4(),
    ficha_treino_id  UUID         NOT NULL,
    numero           INTEGER      NOT NULL,
    criado_em        TIMESTAMP    NOT NULL DEFAULT NOW(),
    deletado_em      TIMESTAMP,
    versao           BIGINT       NOT NULL DEFAULT 0,

    CONSTRAINT pk_versoes_ficha PRIMARY KEY (id),
    CONSTRAINT fk_versao_ficha FOREIGN KEY (ficha_treino_id) REFERENCES fichas_treino(id)
);

-- Constraint crítica: número único por ficha enquanto não deletado
-- Isso garante que não podem existir duas versões ativas com mesmo número
CREATE UNIQUE INDEX uq_versao_numero_ativo
    ON versoes_ficha_treino (ficha_treino_id, numero)
    WHERE deletado_em IS NULL;

CREATE INDEX idx_versao_ficha ON versoes_ficha_treino (ficha_treino_id);

-- FK reversa: ficha aponta para sua versão ativa
ALTER TABLE fichas_treino
    ADD CONSTRAINT fk_ficha_versao_ativa
    FOREIGN KEY (versao_ativa_id) REFERENCES versoes_ficha_treino(id);

-- ==========================================================================
-- TABELA: versao_exercicio (tabela associativa M:N)
-- Liga exercícios a versões de ficha com ordem definida
-- ==========================================================================
CREATE TABLE versao_exercicio (
    versao_ficha_id  UUID    NOT NULL,
    exercicio_id     UUID    NOT NULL,
    ordem            INTEGER NOT NULL DEFAULT 0,

    CONSTRAINT pk_versao_exercicio PRIMARY KEY (versao_ficha_id, exercicio_id),
    CONSTRAINT fk_ve_versao FOREIGN KEY (versao_ficha_id) REFERENCES versoes_ficha_treino(id),
    CONSTRAINT fk_ve_exercicio FOREIGN KEY (exercicio_id) REFERENCES exercicios(id)
);

CREATE INDEX idx_ve_versao ON versao_exercicio (versao_ficha_id);
CREATE INDEX idx_ve_exercicio ON versao_exercicio (exercicio_id);

-- ==========================================================================
-- TABELA: sessoes_treino
-- Execução de uma ficha num momento específico
-- Registra qual versão foi usada (audit trail)
-- ==========================================================================
CREATE TABLE sessoes_treino (
    id                       UUID         NOT NULL DEFAULT uuid_generate_v4(),
    usuario_id               UUID         NOT NULL,
    versao_ficha_treino_id   UUID         NOT NULL,
    data                     DATE         NOT NULL DEFAULT CURRENT_DATE,
    hora_inicio              TIMESTAMP    NOT NULL DEFAULT NOW(),
    hora_fim                 TIMESTAMP,
    observacoes_gerais       VARCHAR(2000),
    status                   VARCHAR(30)  NOT NULL DEFAULT 'EM_PROGRESSO',
    versao                   BIGINT       NOT NULL DEFAULT 0,

    CONSTRAINT pk_sessoes_treino PRIMARY KEY (id),
    CONSTRAINT fk_sessao_versao FOREIGN KEY (versao_ficha_treino_id) REFERENCES versoes_ficha_treino(id),
    CONSTRAINT ck_sessao_status CHECK (status IN ('EM_PROGRESSO', 'FINALIZADO', 'CANCELADO'))
);

CREATE INDEX idx_sessao_usuario ON sessoes_treino (usuario_id, data DESC);
CREATE INDEX idx_sessao_versao_ficha ON sessoes_treino (versao_ficha_treino_id);

-- ==========================================================================
-- TABELA: exercicios_executados
-- Execução de um exercício dentro de uma sessão
-- ==========================================================================
CREATE TABLE exercicios_executados (
    id              UUID         NOT NULL DEFAULT uuid_generate_v4(),
    sessao_id       UUID         NOT NULL,
    exercicio_id    UUID         NOT NULL,
    ordem           INTEGER      NOT NULL DEFAULT 0,
    observacoes     VARCHAR(1000),
    duracao_minutos INTEGER,

    CONSTRAINT pk_exercicios_executados PRIMARY KEY (id),
    CONSTRAINT fk_exec_sessao FOREIGN KEY (sessao_id) REFERENCES sessoes_treino(id),
    CONSTRAINT fk_exec_exercicio FOREIGN KEY (exercicio_id) REFERENCES exercicios(id)
);

CREATE INDEX idx_exec_sessao ON exercicios_executados (sessao_id);
CREATE INDEX idx_exec_exercicio_data ON exercicios_executados (exercicio_id);

-- ==========================================================================
-- TABELA: series_executadas
-- Cada série realizada durante execução de exercício
-- ==========================================================================
CREATE TABLE series_executadas (
    id                    UUID           NOT NULL DEFAULT uuid_generate_v4(),
    exercicio_executado_id UUID          NOT NULL,
    numero_serie          INTEGER        NOT NULL,
    peso                  DECIMAL(8, 2)  NOT NULL,
    repeticoes            INTEGER        NOT NULL,
    horario_execucao      TIMESTAMP      NOT NULL DEFAULT NOW(),
    versao                BIGINT         NOT NULL DEFAULT 0,

    CONSTRAINT pk_series_executadas PRIMARY KEY (id),
    CONSTRAINT fk_serie_exec FOREIGN KEY (exercicio_executado_id) REFERENCES exercicios_executados(id),
    CONSTRAINT ck_serie_peso CHECK (peso >= 0),
    CONSTRAINT ck_serie_reps CHECK (repeticoes >= 0),
    CONSTRAINT ck_serie_numero CHECK (numero_serie > 0)
);

CREATE INDEX idx_serie_exec ON series_executadas (exercicio_executado_id);
CREATE INDEX idx_serie_horario ON series_executadas (horario_execucao DESC);

-- Performance: índice composto para consultas de histórico por exercício
-- Usado em consultas: GET /api/exercicios/{id}/historico
CREATE INDEX idx_historico_exercicio
    ON series_executadas (exercicio_executado_id, horario_execucao DESC);
