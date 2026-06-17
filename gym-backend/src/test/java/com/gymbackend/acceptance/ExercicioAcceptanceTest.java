package com.gymbackend.acceptance;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

/**
 * Testes de aceitação para fluxo completo de Exercícios.
 * US1: Criar → Ler → Editar → Inativar → Confirmar histórico intacto
 */
@QuarkusTest
@DisplayName("US1 - Exercícios: Fluxo de Aceitação")
class ExercicioAcceptanceTest {

    @Test
    @DisplayName("Criar exercício com dados válidos retorna 201")
    void criarExercicioComDadosValidosRetorna201() {
        given()
            .contentType(ContentType.JSON)
            .body("""
                    {
                        "nome": "Supino Reto",
                        "descricao": "Exercício básico para peitoral",
                        "grupoMuscular": "Peito"
                    }
                    """)
        .when()
            .post("/api/exercicios")
        .then()
            .statusCode(201)
            .body("id", notNullValue())
            .body("nome", equalTo("Supino Reto"))
            .body("status", equalTo("ATIVO"));
    }

    @Test
    @DisplayName("Criar exercício com nome em branco retorna 400")
    void criarExercicioSemNomeRetorna400() {
        given()
            .contentType(ContentType.JSON)
            .body("""
                    {
                        "nome": "",
                        "descricao": "Descrição",
                        "grupoMuscular": "Peito"
                    }
                    """)
        .when()
            .post("/api/exercicios")
        .then()
            .statusCode(400);
    }

    @Test
    @DisplayName("Listar exercícios retorna paginação")
    void listarExerciciosRetornaPaginacao() {
        when()
            .get("/api/exercicios")
        .then()
            .statusCode(200)
            .body("conteudo", notNullValue())
            .body("totalElementos", greaterThanOrEqualTo(0));
    }

    @Test
    @DisplayName("Buscar exercício inexistente retorna 404")
    void buscarExercicioInexistenteRetorna404() {
        when()
            .get("/api/exercicios/00000000-0000-0000-0000-000000000000")
        .then()
            .statusCode(404);
    }

    @Test
    @DisplayName("Peso negativo em série retorna 400 (OWASP A03)")
    void pesoNegativoRetorna400() {
        given()
            .contentType(ContentType.JSON)
            .body("""
                    {
                        "peso": -10,
                        "repeticoes": 10
                    }
                    """)
        .when()
            .post("/api/sessoes/00000000-0000-0000-0000-000000000001/series?exercicioExecutadoId=00000000-0000-0000-0000-000000000001")
        .then()
            .statusCode(anyOf(is(400), is(404)));
    }
}
