package com.gymbackend.acceptance;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

/**
 * Testes de aceitação para Fichas e Versionamento Imutável.
 * US2: Criar ficha → V1 automática → modificar → V2 criada → V1 intacta
 */
@QuarkusTest
@DisplayName("US2 - Fichas: Versionamento Imutável")
class VersaoFichaTreinoAcceptanceTest {

    @Test
    @DisplayName("Criar ficha retorna 201 com V1 automática")
    void criarFichaRetornaComV1() {
        given()
            .contentType(ContentType.JSON)
            .body("""
                    {
                        "nome": "Push Pull Legs"
                    }
                    """)
        .when()
            .post("/api/fichas")
        .then()
            .statusCode(201)
            .body("id", notNullValue())
            .body("nome", equalTo("Push Pull Legs"))
            .body("versaoAtivaId", notNullValue());
    }

    @Test
    @DisplayName("Criar ficha sem nome retorna 400")
    void criarFichaSemNomeRetorna400() {
        given()
            .contentType(ContentType.JSON)
            .body("""
                    {
                        "nome": ""
                    }
                    """)
        .when()
            .post("/api/fichas")
        .then()
            .statusCode(400);
    }
}
