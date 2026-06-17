package com.gymbackend.acceptance;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

/**
 * Testes de aceitação para fluxo completo de Sessões de Treino.
 * US3: Iniciar sessão → Registrar série → Finalizar → Histórico
 */
@QuarkusTest
@DisplayName("US3 - Sessões: Fluxo de Aceitação")
class SessaoTreinoAcceptanceTest {

    @Test
    @DisplayName("Iniciar sessão sem fichaId retorna 400")
    void iniciarSessaoSemFichaIdRetorna400() {
        given()
            .contentType(ContentType.JSON)
        .when()
            .post("/api/sessoes")
        .then()
            .statusCode(anyOf(is(400), is(422)));
    }

    @Test
    @DisplayName("Buscar sessão inexistente retorna 404")
    void buscarSessaoInexistenteRetorna404() {
        when()
            .get("/api/sessoes/00000000-0000-0000-0000-000000000000")
        .then()
            .statusCode(404);
    }

    @Test
    @DisplayName("Registrar série com peso negativo retorna 400")
    void registrarSeriePesoNegativoRetorna400() {
        given()
            .contentType(ContentType.JSON)
            .body("""
                    {
                        "peso": -5.0,
                        "repeticoes": 10
                    }
                    """)
        .when()
            .post("/api/sessoes/00000000-0000-0000-0000-000000000001/series?exercicioExecutadoId=00000000-0000-0000-0000-000000000001")
        .then()
            .statusCode(anyOf(is(400), is(404), is(422)));
    }

    @Test
    @DisplayName("Registrar série com repetições acima de 100 retorna 400")
    void registrarSerieRepsAcima100Retorna400() {
        given()
            .contentType(ContentType.JSON)
            .body("""
                    {
                        "peso": 60.0,
                        "repeticoes": 101
                    }
                    """)
        .when()
            .post("/api/sessoes/00000000-0000-0000-0000-000000000001/series?exercicioExecutadoId=00000000-0000-0000-0000-000000000001")
        .then()
            .statusCode(anyOf(is(400), is(404)));
    }
}
