package com.gymbackend;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.TestInstance;

/**
 * Classe base para testes de integração com H2 (perfil %test).
 * Usa banco H2 em memória via application.properties perfil test.
 *
 * Todos os testes herdam este setup:
 * - H2 in-memory com MODE=PostgreSQL
 * - Flyway roda automaticamente
 * - @QuarkusTest inicializa o contexto CDI completo
 */
@QuarkusTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class TesteIntegracaoBase extends TesteUnitarioBase {
    // Herda constantes de TesteUnitarioBase
    // @QuarkusTest configura H2 via application.properties %test profile
}
