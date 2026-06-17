package com.gymbackend.acceptance;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

/**
 * Testes de aceitação para endpoint de Progressão (algoritmo 8-12 RMs).
 * US5: Calcular sugestão de carga baseada no histórico.
 */
@QuarkusTest
@DisplayName("US5 - Progressão: Fluxo de Aceitação")
class ProgressaoAcceptanceTest {

    @Test
    @DisplayName("Progressão de exercício inexistente retorna 404")
    void progressaoExercicioInexistenteRetorna404() {
        when()
            .get("/api/exercicios/00000000-0000-0000-0000-000000000000/progressao")
        .then()
            .statusCode(404);
    }

    @Test
    @DisplayName("Progressão retorna sugestao no corpo da resposta")
    void progressaoRetornaSugestao() {
        // Cria exercício primeiro
        io.restassured.response.Response response = io.restassured.RestAssured.given()
            .contentType("application/json")
            .body("""
                    {
                        "nome": "Exercício Progressão Test",
                        "grupoMuscular": "Peito"
                    }
                    """)
            .post("/api/exercicios");

        if (response.statusCode() == 201) {
            String id = response.jsonPath().getString("id");

            when()
                .get("/api/exercicios/" + id + "/progressao")
            .then()
                .statusCode(200)
                .body("sugestao", equalTo("SEM_HISTORICO"))
                .body("justificativa", notNullValue());
        }
    }
}
