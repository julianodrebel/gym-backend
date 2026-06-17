package com.gymbackend.support;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Helper para testes de API REST usando RestAssured.
 * Simplifica chamadas HTTP com autenticação e content type padrão.
 */
@ApplicationScoped
public class RestAssuredHelper {

    /**
     * Cria requisição base com JSON content type.
     */
    public static RequestSpecification json() {
        return RestAssured.given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON);
    }

    /**
     * Cria requisição autenticada (MVP: sem JWT real, apenas content type).
     */
    public static RequestSpecification autenticado() {
        return json()
                .header("Authorization", "Bearer test-token");
    }

    /**
     * Verifica se a resposta contém erro de validação (400).
     */
    public static void assertErroValidacao(ValidatableResponse response) {
        response.statusCode(400);
    }

    /**
     * Verifica se a resposta é 404 Not Found.
     */
    public static void assertNaoEncontrado(ValidatableResponse response) {
        response.statusCode(404);
    }

    /**
     * Verifica se a resposta é 422 Unprocessable Entity (regra de negócio).
     */
    public static void assertRegraDeNegocio(ValidatableResponse response) {
        response.statusCode(422);
    }
}
