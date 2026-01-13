/* @MENTEE_POWER (C)2026 */
package ru.mentee.library.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.mentee.library.api.dto.BookDto;
import ru.mentee.library.api.dto.BookSearchCriteria;

/**
 * Интерфейс сервиса для продвинутого поиска книг.
 */
public interface BookSearchService {

    /**
     * Выполняет динамический поиск книг по заданным критериям.
     *
     * @param criteria критерии поиска
     * @param pageable параметры пагинации и сортировки
     * @return страница с найденными книгами
     */
    Page<BookDto> searchBooks(BookSearchCriteria criteria, Pageable pageable);
}
