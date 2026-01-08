/* @MENTEE_POWER (C)2025 */
package ru.mentee.library.api.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.mentee.library.api.dto.CreateBookRequest;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("test")
@DisplayName("BookController Integration Tests")
class BookControllerIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:15")
                    .withDatabaseName("testdb")
                    .withUsername("test")
                    .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        registry.add("spring.liquibase.enabled", () -> "false");
    }

    @Autowired private MockMvc mockMvc;

    @Autowired private ObjectMapper objectMapper;

    @Test
    @DisplayName("Should create book with valid data")
    void shouldCreateBookWithValidData() throws Exception {
        // Given
        CreateBookRequest request = new CreateBookRequest();
        request.setTitle("Test Book");
        request.setAuthor("Test Author");
        request.setIsbn("978-3-16-148410-0");
        request.setPublishedDate(LocalDate.of(2024, 1, 1));

        String requestJson = objectMapper.writeValueAsString(request);

        // When & Then
        mockMvc.perform(
                        post("/api/books")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));
    }

    @Test
    @DisplayName("Should return 400 when ISBN is invalid")
    void shouldReturn400WhenIsbnIsInvalid() throws Exception {
        // Given
        CreateBookRequest request = new CreateBookRequest();
        request.setTitle("Test Book");
        request.setAuthor("Test Author");
        request.setIsbn("invalid-isbn");

        String requestJson = objectMapper.writeValueAsString(request);

        // When & Then
        mockMvc.perform(
                        post("/api/books")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 400 when title is missing")
    void shouldReturn400WhenTitleIsMissing() throws Exception {
        // Given
        CreateBookRequest request = new CreateBookRequest();
        request.setAuthor("Test Author");
        request.setIsbn("978-3-16-148410-0");

        String requestJson = objectMapper.writeValueAsString(request);

        // When & Then
        mockMvc.perform(
                        post("/api/books")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should get all books")
    void shouldGetAllBooks() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/books")).andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should get books filtered by author")
    void shouldGetBooksFilteredByAuthor() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/books").param("author", "Test Author"))
                .andExpect(status().isOk());
    }
}
