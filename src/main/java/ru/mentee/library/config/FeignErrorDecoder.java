/* @MENTEE_POWER (C)2026 */
package ru.mentee.library.config;

import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

/**
 * Кастомный декодер ошибок для Feign клиентов.
 */
@Slf4j
@Component
public class FeignErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        HttpStatus status = HttpStatus.valueOf(response.status());
        String message =
                String.format("Ошибка при вызове %s: %s", methodKey, status.getReasonPhrase());

        log.warn("Feign ошибка: {} - {}", methodKey, status);

        switch (status) {
            case BAD_REQUEST:
                return new ResponseStatusException(HttpStatus.BAD_REQUEST, "Некорректный запрос");
            case NOT_FOUND:
                return new ResponseStatusException(HttpStatus.NOT_FOUND, "Ресурс не найден");
            case INTERNAL_SERVER_ERROR:
                return new ResponseStatusException(
                        HttpStatus.INTERNAL_SERVER_ERROR, "Внутренняя ошибка сервера");
            default:
                return new ResponseStatusException(status, message);
        }
    }
}
