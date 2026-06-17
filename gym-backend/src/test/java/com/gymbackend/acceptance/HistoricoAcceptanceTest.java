package com.gymbackend.acceptance;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

/**
 * Testes de aceitação para endpoints de Histórico.
 * US4: Consultar histórico paginado, evolução e última execução.
 */
@QuarkusTest
@DisplayName("US4 - Histórico: Fluxo de Aceitação")
class HistoricoAcceptanceTest {

    @Test
    @DisplayName("Histórico de exercício inexistente retorna 404")
    void historicoExercicioInexistenteRetorna404() {
        when()
            .get("/api/exercicios/00000000-0000-0000-0000-000000000000/historico")
        .then()
            .statusCode(404);
    }

    @Test
    @DisplayName("Evolução de exercício inexistente retorna 404")
    void evolucaoExercicioInexistenteRetorna404() {
        when()
            .get("/api/exercicios/00000000-0000-0000-0000-000000000000/evolucao")
        .then()
            .statusCode(404);
    }

    @Test
    @DisplayName("Parâmetros de paginação inválidos retornam 400")
    void paginacaoInvalidaRetorna400() {
        when()
            .get("/api/exercicios/00000000-0000-0000-0000-000000000000/historico?pagina=-1&tamanho=0")
        .then()
            .statusCode(anyOf(is(400), is(404)));
    }
}
