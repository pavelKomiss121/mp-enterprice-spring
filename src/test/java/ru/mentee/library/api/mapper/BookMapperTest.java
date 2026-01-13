/* @MENTEE_POWER (C)2026 */
package ru.mentee.library.api.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import ru.mentee.library.api.dto.BookDto;
import ru.mentee.library.domain.model.Author;
import ru.mentee.library.domain.model.Book;
import ru.mentee.library.domain.model.BookStatus;
import ru.mentee.library.domain.model.Category;

@SpringBootTest
@AutoConfigureWireMock(port = 0)
@TestPropertySource(properties = {"openlibrary.api.url=http://localhost:${wiremock.server.port}"})
class BookMapperTest {

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
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

    @Autowired private BookMapper bookMapper;

    @Test
    @DisplayName("Should корректно мапить Entity в DTO")
    void shouldMapEntityToDto() {
        // Given
        Category category =
                Category.builder().id(1L).name("Fiction").description("Fiction category").build();

        Author author1 =
                Author.builder()
                        .id(1L)
                        .firstName("John")
                        .lastName("Doe")
                        .biography("Author biography")
                        .build();

        Author author2 =
                Author.builder()
                        .id(2L)
                        .firstName("Jane")
                        .lastName("Smith")
                        .biography("Another biography")
                        .build();

        Set<Author> authors = new HashSet<>();
        authors.add(author1);
        authors.add(author2);

        Book book =
                Book.builder()
                        .id(1L)
                        .isbn("978-0-123456-78-9")
                        .title("Test Book")
                        .description("Test Description")
                        .publicationYear(2020)
                        .pages(300)
                        .status(BookStatus.AVAILABLE)
                        .category(category)
                        .authors(authors)
                        .createdAt(Instant.now())
                        .updatedAt(Instant.now())
                        .build();

        // When
        BookDto dto = bookMapper.toDto(book);

        // Then
        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(book.getId());
        assertThat(dto.getIsbn()).isEqualTo(book.getIsbn());
        assertThat(dto.getTitle()).isEqualTo(book.getTitle());
        assertThat(dto.getDescription()).isEqualTo(book.getDescription());
        assertThat(dto.getPublicationYear()).isEqualTo(book.getPublicationYear());
        assertThat(dto.getPages()).isEqualTo(book.getPages());
        assertThat(dto.getStatus()).isEqualTo(book.getStatus());
        assertThat(dto.getCreatedAt()).isEqualTo(book.getCreatedAt());
        assertThat(dto.getUpdatedAt()).isEqualTo(book.getUpdatedAt());

        assertThat(dto.getCategory()).isNotNull();
        assertThat(dto.getCategory().getId()).isEqualTo(category.getId());
        assertThat(dto.getCategory().getName()).isEqualTo(category.getName());
        assertThat(dto.getCategory().getDescription()).isEqualTo(category.getDescription());

        assertThat(dto.getAuthors()).isNotNull();
        assertThat(dto.getAuthors()).hasSize(2);
    }

    @Test
    @DisplayName("Should корректно обрабатывать null Entity")
    void shouldHandleNullEntity() {
        // When
        BookDto dto = bookMapper.toDto(null);

        // Then
        assertThat(dto).isNull();
    }

    @Test
    @DisplayName("Should корректно обрабатывать Entity без категории и авторов")
    void shouldMapEntityWithoutCategoryAndAuthors() {
        // Given
        Book book =
                Book.builder()
                        .id(1L)
                        .isbn("978-0-123456-78-9")
                        .title("Test Book")
                        .description("Test Description")
                        .publicationYear(2020)
                        .pages(300)
                        .status(BookStatus.AVAILABLE)
                        .createdAt(Instant.now())
                        .updatedAt(Instant.now())
                        .build();

        // When
        BookDto dto = bookMapper.toDto(book);

        // Then
        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(book.getId());
        assertThat(dto.getCategory()).isNull();
        assertThat(dto.getAuthors()).isNullOrEmpty();
    }
}
