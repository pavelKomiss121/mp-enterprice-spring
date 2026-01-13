/* @MENTEE_POWER (C)2026 */
package ru.mentee.library.service;

import ru.mentee.library.api.dto.BookApiResponse;

/**
 * Интерфейс сервиса для получения информации о книгах из внешнего API.
 */
public interface BookInfoService {

    /**
     * Получает информацию о книге по ISBN из Open Library API.
     *
     * @param isbn ISBN книги
     * @return информация о книге или null, если книга не найдена
     */
    BookApiResponse getBookByIsbn(String isbn);
}
