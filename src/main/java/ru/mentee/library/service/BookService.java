/* @MENTEE_POWER (C)2026 */
package ru.mentee.library.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.mentee.library.api.dto.BookDto;

/**
 * Интерфейс сервиса для работы с книгами.
 */
public interface BookService {

    /**
     * Получает все книги с пагинацией.
     *
     * @param pageable параметры пагинации
     * @return страница с книгами
     */
    Page<BookDto> getAllBooks(Pageable pageable);

    /**
     * Создает новую книгу.
     *
     * @param bookDto DTO книги
     * @return созданная книга
     */
    BookDto createBook(BookDto bookDto);

    /**
     * Обновляет книгу.
     *
     * @param id идентификатор книги
     * @param bookDto DTO книги
     * @return обновленная книга
     */
    BookDto updateBook(Long id, BookDto bookDto);

    /**
     * Удаляет книгу.
     *
     * @param id идентификатор книги
     */
    void deleteBook(Long id);
}
