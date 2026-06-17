package com.gymbackend;

import org.junit.jupiter.api.TestInstance;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Classe base abstrata para testes unitários.
 * Fornece constantes e utilitários comuns.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class TesteUnitarioBase {

    protected static final UUID ID_EXERCICIO = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
    protected static final UUID ID_FICHA = UUID.fromString("660e8400-e29b-41d4-a716-446655440001");
    protected static final UUID ID_VERSAO = UUID.fromString("770e8400-e29b-41d4-a716-446655440002");
    protected static final UUID ID_SESSAO = UUID.fromString("880e8400-e29b-41d4-a716-446655440003");
    protected static final UUID ID_USUARIO = UUID.fromString("00000000-0000-0000-0000-000000000001");

    protected static final BigDecimal PESO_60KG = new BigDecimal("60.0");
    protected static final BigDecimal PESO_62_5KG = new BigDecimal("62.5");
    protected static final int REPS_10 = 10;
    protected static final int REPS_12 = 12;
    protected static final int REPS_8 = 8;
    protected static final int REPS_7 = 7;
}
