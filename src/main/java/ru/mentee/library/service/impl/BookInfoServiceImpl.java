/* @MENTEE_POWER (C)2026 */
package ru.mentee.library.service.impl;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.mentee.library.api.client.OpenLibraryClient;
import ru.mentee.library.api.dto.BookApiResponse;
import ru.mentee.library.service.BookInfoService;

/**
 * Реализация сервиса для получения информации о книгах из внешнего API по ISBN.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BookInfoServiceImpl implements BookInfoService {

    private final OpenLibraryClient openLibraryClient;

    @Override
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
