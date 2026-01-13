/* @MENTEE_POWER (C)2026 */
package ru.mentee.library.api.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.mentee.library.api.dto.CreateUserDto;
import ru.mentee.library.api.dto.UserDto;
import ru.mentee.library.domain.model.User;

/**
 * MapStruct маппер для преобразования User сущностей в DTO.
 */
@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto toDto(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "registeredAt", ignore = true)
    @Mapping(target = "password", ignore = true)
    User toEntity(CreateUserDto createUserDto);
}
