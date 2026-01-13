/* @MENTEE_POWER (C)2026 */
package ru.mentee.library.service;

import ru.mentee.library.api.dto.LoginRequest;
import ru.mentee.library.api.dto.LoginResponse;

/**
 * Интерфейс сервиса аутентификации.
 */
public interface AuthService {

    /**
     * Выполняет аутентификацию пользователя.
     *
     * @param request данные для входа
     * @return ответ с токенами доступа
     */
    LoginResponse login(LoginRequest request);

    /**
     * Обновляет access token используя refresh token.
     *
     * @param refreshTokenValue значение refresh token
     * @return ответ с новыми токенами доступа
     */
    LoginResponse refresh(String refreshTokenValue);
}
