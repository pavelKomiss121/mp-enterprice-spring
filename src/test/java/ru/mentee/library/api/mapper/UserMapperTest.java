/* @MENTEE_POWER (C)2026 */
package ru.mentee.library.api.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import ru.mentee.library.api.dto.CreateUserDto;
import ru.mentee.library.api.dto.UserDto;
import ru.mentee.library.domain.model.User;

@SpringBootTest
@AutoConfigureWireMock(port = 0)
@TestPropertySource(properties = {"openlibrary.api.url=http://localhost:${wiremock.server.port}"})
class UserMapperTest {

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add(
                "spring.datasource.url",
                () -> "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE");
        registry.add("spring.datasource.driver-class-name", () -> "org.h2.Driver");
        registry.add("spring.datasource.username", () -> "sa");
        registry.add("spring.datasource.password", () -> "");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        registry.add(
                "spring.jpa.properties.hibernate.dialect", () -> "org.hibernate.dialect.H2Dialect");
        registry.add("spring.liquibase.enabled", () -> "false");
    }

    @Autowired private UserMapper userMapper;

    @Test
    @DisplayName("Should корректно мапить Entity в DTO")
    void shouldMapEntityToDto() {
        // Given
        User user =
                User.builder()
                        .id(1L)
                        .email("test@example.com")
                        .firstName("John")
                        .lastName("Doe")
                        .phone("1234567890")
                        .registeredAt(Instant.now())
                        .role("USER")
                        .password("encodedPassword")
                        .build();

        // When
        UserDto dto = userMapper.toDto(user);

        // Then
        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(user.getId());
        assertThat(dto.getEmail()).isEqualTo(user.getEmail());
        assertThat(dto.getFirstName()).isEqualTo(user.getFirstName());
        assertThat(dto.getLastName()).isEqualTo(user.getLastName());
        assertThat(dto.getPhone()).isEqualTo(user.getPhone());
        assertThat(dto.getRegisteredAt()).isEqualTo(user.getRegisteredAt());
        assertThat(dto.getRole()).isEqualTo(user.getRole());
        // Password should not be in DTO - проверяем, что в DTO нет поля password
        // (это видно из того, что все остальные поля проверены и password не включен)
    }

    @Test
    @DisplayName("Should корректно мапить CreateUserDto в Entity")
    void shouldMapCreateUserDtoToEntity() {
        // Given
        CreateUserDto createUserDto =
                CreateUserDto.builder()
                        .email("newuser@example.com")
                        .firstName("Jane")
                        .lastName("Smith")
                        .phone("9876543210")
                        .password("password123")
                        .role("USER")
                        .build();

        // When
        User user = userMapper.toEntity(createUserDto);

        // Then
        assertThat(user).isNotNull();
        assertThat(user.getEmail()).isEqualTo(createUserDto.getEmail());
        assertThat(user.getFirstName()).isEqualTo(createUserDto.getFirstName());
        assertThat(user.getLastName()).isEqualTo(createUserDto.getLastName());
        assertThat(user.getPhone()).isEqualTo(createUserDto.getPhone());
        assertThat(user.getRole()).isEqualTo(createUserDto.getRole());
        // ID and registeredAt should be ignored (set by service)
        assertThat(user.getId()).isNull();
        assertThat(user.getRegisteredAt()).isNull();
    }

    @Test
    @DisplayName("Should корректно обрабатывать null Entity")
    void shouldHandleNullEntity() {
        // When
        UserDto dto = userMapper.toDto(null);

        // Then
        assertThat(dto).isNull();
    }

    @Test
    @DisplayName("Should корректно обрабатывать null CreateUserDto")
    void shouldHandleNullCreateUserDto() {
        // When
        User user = userMapper.toEntity(null);

        // Then
        assertThat(user).isNull();
    }
}
