/* @MENTEE_POWER (C)2025 */
package ru.mentee.banking.aspect;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import ru.mentee.banking.api.dto.BalanceDto;
import ru.mentee.banking.exception.AccessDeniedException;
import ru.mentee.banking.service.AccountService;
import ru.mentee.banking.service.SecurityContext;

@SpringBootTest(classes = ru.mentee.banking.BankingApplication.class)
@EnableAspectJAutoProxy
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@TestPropertySource(
        properties = {
            "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration"
        })
class SecurityAspectTest {

    @Autowired private AccountService accountService;

    @SpyBean private SecurityContext securityContext;

    @BeforeEach
    void setUp() {
        securityContext.getRoles().clear();
    }

    @Test
    @DisplayName("Should разрешить доступ для роли ADMIN")
    void shouldAllowAccessForAdminRole() {
        // Given
        securityContext.getRoles().add("ADMIN");

        // When
        BalanceDto balance = accountService.getBalance("ACC001");

        // Then
        assertThat(balance).isNotNull();
    }

    @Test
    @DisplayName("Should запретить доступ без нужной роли")
    void shouldDenyAccessWithoutRole() {
        // Given - роли уже очищены в setUp()

        // When & Then
        assertThatThrownBy(() -> accountService.getBalance("ACC001"))
                .isInstanceOf(AccessDeniedException.class);
    }
}
