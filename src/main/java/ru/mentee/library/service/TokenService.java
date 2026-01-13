/* @MENTEE_POWER (C)2026 */
package ru.mentee.library.service;

import org.springframework.security.core.userdetails.UserDetails;

/**
 * Интерфейс сервиса для работы с JWT токенами.
 */
public interface TokenService {

    /**
     * Генерирует access token для пользователя.
     *
     * @param userDetails детали пользователя
     * @return access token
     */
    String generateAccessToken(UserDetails userDetails);

    /**
     * Генерирует refresh token для пользователя.
     *
     * @param userDetails детали пользователя
     * @return refresh token
     */
    String generateRefreshToken(UserDetails userDetails);

    /**
     * Извлекает username из токена.
     *
     * @param token JWT токен
     * @return username
     */
    String getUsernameFromToken(String token);

    /**
     * Проверяет валидность токена.
     *
     * @param token JWT токен
     * @return true если токен валиден
     */
    boolean isTokenValid(String token);

    /**
     * Возвращает время жизни refresh token в секундах.
     *
     * @return время жизни в секундах
     */
    Long getRefreshTokenExpiration();
}
