/* @MENTEE_POWER (C)2026 */
package ru.mentee.library.service;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.mentee.library.api.client.OpenLibraryClient;
import ru.mentee.library.api.dto.BookApiResponse;

/**
 * Сервис для получения информации о книгах из внешнего API по ISBN.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BookInfoService {

    private final OpenLibraryClient openLibraryClient;

    /**
     * Получает информацию о книге по ISBN из Open Library API.
     *
     * @param isbn ISBN книги
     * @return информация о книге или null, если книга не найдена
     */
    public BookApiResponse getBookByIsbn(String isbn) {
        log.debug("Запрос информации о книге по ISBN: {}", isbn);
        try {
            String bibkeys = "ISBN:" + isbn;
            Map<String, BookApiResponse> response =
                    openLibraryClient.getBookByIsbn(bibkeys, "json", "data");

            if (response == null || response.isEmpty()) {
                log.warn("Книга с ISBN {} не найдена", isbn);
                return null;
            }

            BookApiResponse bookInfo = response.get(bibkeys);
            if (bookInfo == null) {
                log.warn("Книга с ISBN {} не найдена в ответе API", isbn);
                return null;
            }

            log.debug("Информация о книге получена: {}", bookInfo.getTitle());
            return bookInfo;
        } catch (Exception e) {
            log.error(
                    "Ошибка при получении информации о книге по ISBN {}: {}", isbn, e.getMessage());
            throw new RuntimeException("Не удалось получить информацию о книге", e);
        }
    }
}
