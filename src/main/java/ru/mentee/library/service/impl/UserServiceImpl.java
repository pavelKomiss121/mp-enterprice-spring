/* @MENTEE_POWER (C)2026 */
package ru.mentee.library.service.impl;

import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mentee.library.api.dto.CreateUserDto;
import ru.mentee.library.api.dto.UserDto;
import ru.mentee.library.api.mapper.UserMapper;
import ru.mentee.library.domain.model.User;
import ru.mentee.library.domain.repository.UserRepository;
import ru.mentee.library.service.UserService;

/**
 * Реализация сервиса для работы с пользователями.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream().map(userMapper::toDto).toList();
    }

    @Override
    public UserDto getUserById(Long id) {
        return userRepository
                .findById(id)
                .map(userMapper::toDto)
                .orElseThrow(() -> new RuntimeException("User not found: " + id));
    }

    @Override
    @Transactional
    public UserDto createUser(CreateUserDto createUserDto) {
        User user = userMapper.toEntity(createUserDto);
        user.setPassword(passwordEncoder.encode(createUserDto.getPassword()));
        user.setRegisteredAt(Instant.now());
        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }
}
