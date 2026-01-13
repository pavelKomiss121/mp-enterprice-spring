/* @MENTEE_POWER (C)2026 */
package ru.mentee.library.service;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import ru.mentee.library.api.dto.BookApiResponse;

@SpringBootTest
@AutoConfigureWireMock(port = 0)
@TestPropertySource(properties = {"openlibrary.api.url=http://localhost:${wiremock.server.port}"})
class BookInfoServiceTest {

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        // H2 in-memory database
        registry.add(
                "spring.datasource.url",
                () -> "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE");
        registry.add("spring.datasource.driver-class-name", () -> "org.h2.Driver");
        registry.add("spring.datasource.username", () -> "sa");
        registry.add("spring.datasource.password", () -> "");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        registry.add(
                "spring.jpa.properties.hibernate.dialect", () -> "org.hibernate.dialect.H2Dialect");
        registry.add("spring.liquibase.enabled", () -> "false");
    }

    @Autowired private BookInfoService bookInfoService;

    @Test
    @DisplayName("Should получить информацию о книге по ISBN")
    void shouldGetBookInfoByIsbn() {
        // Given: настраиваем мок-ответ от WireMock
        // Используем urlPathEqualTo с проверкой query параметров
        WireMock.stubFor(
                get(urlPathEqualTo("/api/books"))
                        .withQueryParam("bibkeys", WireMock.equalTo("ISBN:0451524934"))
                        .withQueryParam("format", WireMock.equalTo("json"))
                        .withQueryParam("jscmd", WireMock.equalTo("data"))
                        .willReturn(
                                aResponse()
                                        .withStatus(200)
                                        .withHeader("Content-Type", "application/json")
                                        .withBody(
                                                """
                        {
                          "ISBN:0451524934": {
                            "title": "1984",
                            "authors": [
                              {
                                "name": "George Orwell"
                              }
                            ],
                            "publishers": [
                              {
                                "name": "Signet Classic"
                              }
                            ],
                            "publish_date": "1961",
                            "number_of_pages": 268,
                            "identifiers": {
                              "isbn_13": ["9780451524935"],
                              "isbn_10": ["0451524934"]
                            },
                            "description": "A dystopian novel"
                          }
                        }
                        """)));

        // When
        BookApiResponse response = bookInfoService.getBookByIsbn("0451524934");

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getTitle()).isEqualTo("1984");
        assertThat(response.getAuthors()).isNotEmpty();
        assertThat(response.getAuthors().get(0).getName()).isEqualTo("George Orwell");
        assertThat(response.getPublishers()).isNotEmpty();
        assertThat(response.getPublishers().get(0).getName()).isEqualTo("Signet Classic");
        assertThat(response.getNumberOfPages()).isEqualTo(268);
    }

    @Test
    @DisplayName("Should вернуть null если книга не найдена")
    void shouldReturnNullWhenBookNotFound() {
        // Given: настраиваем мок-ответ с пустым ответом
        WireMock.stubFor(
                get(urlPathEqualTo("/api/books"))
                        .withQueryParam("bibkeys", WireMock.equalTo("ISBN:9999999999"))
                        .withQueryParam("format", WireMock.equalTo("json"))
                        .withQueryParam("jscmd", WireMock.equalTo("data"))
                        .willReturn(
                                aResponse()
                                        .withStatus(200)
                                        .withHeader("Content-Type", "application/json")
                                        .withBody("{}")));

        // When
        BookApiResponse response = bookInfoService.getBookByIsbn("9999999999");

        // Then
        assertThat(response).isNull();
    }

    @Test
    @DisplayName("Should обработать ошибку 404 Not Found")
    void shouldHandle404NotFound() {
        // Given: настраиваем мок-ответ с 404
        WireMock.stubFor(
                get(urlPathEqualTo("/api/books"))
                        .withQueryParam("bibkeys", WireMock.equalTo("ISBN:0000000000"))
                        .withQueryParam("format", WireMock.equalTo("json"))
                        .withQueryParam("jscmd", WireMock.equalTo("data"))
                        .willReturn(aResponse().withStatus(404)));

        // When/Then
        assertThatThrownBy(() -> bookInfoService.getBookByIsbn("0000000000"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Не удалось получить информацию о книге");
    }
}
