/* @MENTEE_POWER (C)2026 */
package ru.mentee.library.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.mentee.library.api.dto.BookDto;
import ru.mentee.library.domain.model.Book;

/**
 * MapStruct маппер для преобразования Book сущностей в BookDto.
 */
@Mapper(componentModel = "spring")
public interface BookMapper {

    @Mapping(target = "category", source = "category")
    @Mapping(target = "authors", source = "authors")
    BookDto toDto(Book book);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "authors", ignore = true)
    Book toEntity(BookDto bookDto);

    BookDto.CategoryDto toCategoryDto(ru.mentee.library.domain.model.Category category);

    BookDto.AuthorDto toAuthorDto(ru.mentee.library.domain.model.Author author);
}
