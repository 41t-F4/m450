package ch.tbz.m450.testing.tools.controller;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.notNullValue;

/**
 * Automation Testing – Übung 1: Die REST-Schnittstelle des Backends
 * automatisiert testen.
 *
 * Tool: REST Assured. Die Tests starten das echte Spring-Boot-Backend auf
 * einem zufälligen Port (inkl. h2-Datenbank) und feuern echte HTTP-Requests
 * dagegen – ein automatisierter API-/Integrationstest, der bei jedem
 * "./mvnw test" (und damit in jeder CI-Pipeline) mitläuft.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class StudentApiRestAssuredTest {

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Test
    @DisplayName("GET /students antwortet mit 200 und JSON-Array")
    void getStudents_returns200AndJsonArray() {
        when()
                .get("/students")
        .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("size()", greaterThanOrEqualTo(0));
    }

    @Test
    @DisplayName("POST /students legt einen Studenten an, GET liefert ihn zurück")
    void postStudent_thenGetContainsStudent() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                        {"name": "Nicola Wehrli", "email": "nicola@tbz.ch"}
                        """)
        .when()
                .post("/students")
        .then()
                .statusCode(200);

        when()
                .get("/students")
        .then()
                .statusCode(200)
                .body("name", hasItem("Nicola Wehrli"))
                .body("email", hasItem("nicola@tbz.ch"));
    }

    @Test
    @DisplayName("POST /students vergibt eine generierte Id")
    void postStudent_assignsGeneratedId() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                        {"name": "Anna Muster", "email": "anna@tbz.ch"}
                        """)
        .when()
                .post("/students")
        .then()
                .statusCode(200);

        when()
                .get("/students")
        .then()
                .statusCode(200)
                .body("find { it.name == 'Anna Muster' }.id", notNullValue())
                .body("find { it.name == 'Anna Muster' }.email", equalTo("anna@tbz.ch"));
    }
}
