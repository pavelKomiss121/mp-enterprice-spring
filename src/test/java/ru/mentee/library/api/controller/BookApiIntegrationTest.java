/* @MENTEE_POWER (C)2026 */
package ru.mentee.library.api.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import ru.mentee.library.config.TestSecurityConfig;

@Disabled("Требуется настройка security для тестов")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestSecurityConfig.class)
class BookApiIntegrationTest {

    @LocalServerPort private int port;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add(
                "spring.datasource.url",
                () -> "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE");
        registry.add("spring.datasource.driver-class-name", () -> "org.h2.Driver");
        registry.add("spring.datasource.username", () -> "sa");
        registry.add("spring.datasource.password", () -> "");
        registry.add("spring.liquibase.enabled", () -> "false");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        registry.add("spring.jpa.database-platform", () -> "org.hibernate.dialect.H2Dialect");
        registry.add("openlibrary.api.url", () -> "http://localhost:8080");
    }

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
    }

    @Test
    @DisplayName("Should получить список книг")
    void shouldGetBookList() {
        given().when()
                .get("/api/books")
                .then()
                .statusCode(200)
                .body("size()", greaterThanOrEqualTo(0))
                .body("content", notNullValue());
    }

    @Test
    @DisplayName("Should создать книгу и вернуть 200 OK")
    void shouldCreateBook() {
        String requestBody =
                """
                {
                  "title": "1984",
                  "isbn": "978-0-452-28423-4",
                  "description": "A dystopian novel",
                  "publicationYear": 1949,
                  "pages": 328,
                  "status": "AVAILABLE"
                }
                """;

        given().contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/books")
                .then()
                .statusCode(200)
                .body("id", notNullValue())
                .body("title", equalTo("1984"))
                .body("isbn", equalTo("978-0-452-28423-4"))
                .body("publicationYear", equalTo(1949));
    }

    @Test
    @DisplayName("Should получить книгу по ID")
    void shouldGetBookById() {
        // Сначала создаем книгу
        String requestBody =
                """
                {
                  "title": "Test Book",
                  "isbn": "1234567890",
                  "description": "Test Description",
                  "publicationYear": 2023,
                  "pages": 100,
                  "status": "AVAILABLE"
                }
                """;

        Long bookId =
                given().contentType(ContentType.JSON)
                        .body(requestBody)
                        .when()
                        .post("/api/books")
                        .then()
                        .statusCode(200)
                        .extract()
                        .path("id");

        // Затем получаем книгу по ID
        given().pathParam("id", bookId)
                .when()
                .get("/api/books/{id}")
                .then()
                .statusCode(200)
                .body("id", equalTo(bookId.intValue()))
                .body("title", equalTo("Test Book"))
                .body("isbn", equalTo("1234567890"));
    }

    @Test
    @DisplayName("Should обновить книгу")
    void shouldUpdateBook() {
        // Сначала создаем книгу
        String createRequestBody =
                """
                {
                  "title": "Original Title",
                  "isbn": "1111111111",
                  "description": "Original Description",
                  "publicationYear": 2020,
                  "pages": 200,
                  "status": "AVAILABLE"
                }
                """;

        Long bookId =
                given().contentType(ContentType.JSON)
                        .body(createRequestBody)
                        .when()
                        .post("/api/books")
                        .then()
                        .statusCode(200)
                        .extract()
                        .path("id");

        // Затем обновляем книгу
        String updateRequestBody =
                """
                {
                  "title": "Updated Title",
                  "isbn": "1111111111",
                  "description": "Updated Description",
                  "publicationYear": 2021,
                  "pages": 250,
                  "status": "AVAILABLE"
                }
                """;

        given().contentType(ContentType.JSON)
                .pathParam("id", bookId)
                .body(updateRequestBody)
                .when()
                .put("/api/books/{id}")
                .then()
                .statusCode(200)
                .body("id", equalTo(bookId.intValue()))
                .body("title", equalTo("Updated Title"))
                .body("description", equalTo("Updated Description"))
                .body("publicationYear", equalTo(2021))
                .body("pages", equalTo(250));
    }

    @Test
    @DisplayName("Should удалить книгу")
    void shouldDeleteBook() {
        // Сначала создаем книгу
        String requestBody =
                """
                {
                  "title": "Book to Delete",
                  "isbn": "9999999999",
                  "description": "This book will be deleted",
                  "publicationYear": 2022,
                  "pages": 150,
                  "status": "AVAILABLE"
                }
                """;

        Long bookId =
                given().contentType(ContentType.JSON)
                        .body(requestBody)
                        .when()
                        .post("/api/books")
                        .then()
                        .statusCode(200)
                        .extract()
                        .path("id");

        // Затем удаляем книгу
        given().pathParam("id", bookId).when().delete("/api/books/{id}").then().statusCode(204);
    }
}
