/* @MENTEE_POWER (C)2026 */
package ru.mentee.library.service;

import java.util.List;
import ru.mentee.library.api.dto.CreateUserDto;
import ru.mentee.library.api.dto.UserDto;

/**
 * Интерфейс сервиса для работы с пользователями.
 */
public interface UserService {

    /**
     * Получает всех пользователей.
     *
     * @return список пользователей
     */
    List<UserDto> getAllUsers();

    /**
     * Получает пользователя по идентификатору.
     *
     * @param id идентификатор пользователя
     * @return пользователь
     */
    UserDto getUserById(Long id);

    /**
     * Создает нового пользователя.
     *
     * @param createUserDto DTO для создания пользователя
     * @return созданный пользователь
     */
    UserDto createUser(CreateUserDto createUserDto);
}
