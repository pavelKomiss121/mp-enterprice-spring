/* @MENTEE_POWER (C)2026 */
package ru.mentee.library.api.mapper;

import java.util.stream.Collectors;
import ru.mentee.library.api.dto.BookDto;
import ru.mentee.library.domain.model.Author;
import ru.mentee.library.domain.model.Book;
import ru.mentee.library.domain.model.Category;

/**
 * Маппер для преобразования Book сущностей в BookDto.
 */
public class BookMapper {

    /**
     * Преобразует Book сущность в BookDto.
     *
     * @param book сущность книги
     * @return DTO книги
     */
    public static BookDto toDto(Book book) {
        if (book == null) {
            return null;
        }

        return BookDto.builder()
                .id(book.getId())
                .isbn(book.getIsbn())
                .title(book.getTitle())
                .description(book.getDescription())
                .publicationYear(book.getPublicationYear())
                .pages(book.getPages())
                .status(book.getStatus())
                .createdAt(book.getCreatedAt())
                .updatedAt(book.getUpdatedAt())
                .category(toCategoryDto(book.getCategory()))
                .authors(
                        book.getAuthors() != null
                                ? book.getAuthors().stream()
                                        .map(BookMapper::toAuthorDto)
                                        .collect(Collectors.toSet())
                                : null)
                .build();
    }

    /**
     * Преобразует Category сущность в CategoryDto.
     *
     * @param category сущность категории
     * @return DTO категории
     */
    private static BookDto.CategoryDto toCategoryDto(Category category) {
        if (category == null) {
            return null;
        }

        return BookDto.CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .build();
    }

    /**
     * Преобразует Author сущность в AuthorDto.
     *
     * @param author сущность автора
     * @return DTO автора
     */
    private static BookDto.AuthorDto toAuthorDto(Author author) {
        if (author == null) {
            return null;
        }

        return BookDto.AuthorDto.builder()
                .id(author.getId())
                .firstName(author.getFirstName())
                .lastName(author.getLastName())
                .biography(author.getBiography())
                .build();
    }
}
