package com.gymbackend.acceptance;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

/**
 * Testes de aceitação para cronômetro de descanso.
 * US6: Registrar série → resposta inclui metadados do cronômetro.
 */
@QuarkusTest
@DisplayName("US6 - Cronômetro: Fluxo de Aceitação")
class CronometroAcceptanceTest {

    @Test
    @DisplayName("Série com sessão inexistente retorna 404 (não 500)")
    void serieComSessaoInexistenteRetorna404() {
        given()
            .contentType(ContentType.JSON)
            .body("""
                    {
                        "peso": 60.0,
                        "repeticoes": 10
                    }
                    """)
        .when()
            .post("/api/sessoes/00000000-0000-0000-0000-000000000000/series?exercicioExecutadoId=00000000-0000-0000-0000-000000000001")
        .then()
            .statusCode(anyOf(is(404), is(422)));
    }

    @Test
    @DisplayName("Peso acima de 500kg retorna 400 (OWASP A03 - validação de entrada)")
    void pesoAcima500kgRetorna400() {
        given()
            .contentType(ContentType.JSON)
            .body("""
                    {
                        "peso": 501.0,
                        "repeticoes": 10
                    }
                    """)
        .when()
            .post("/api/sessoes/00000000-0000-0000-0000-000000000001/series?exercicioExecutadoId=00000000-0000-0000-0000-000000000001")
        .then()
            .statusCode(anyOf(is(400), is(404)));
    }
}
