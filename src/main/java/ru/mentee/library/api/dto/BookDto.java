/* @MENTEE_POWER (C)2026 */
package ru.mentee.library.api.dto;

import java.time.Instant;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.mentee.library.domain.model.BookStatus;

/**
 * DTO для представления книги в API.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookDto {

    private Long id;
    private String isbn;
    private String title;
    private String description;
    private Integer publicationYear;
    private Integer pages;
    private BookStatus status;
    private Instant createdAt;
    private Instant updatedAt;

    /** Информация о категории */
    private CategoryDto category;

    /** Список авторов */
    private Set<AuthorDto> authors;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryDto {
        private Long id;
        private String name;
        private String description;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AuthorDto {
        private Long id;
        private String firstName;
        private String lastName;
        private String biography;
    }
}
