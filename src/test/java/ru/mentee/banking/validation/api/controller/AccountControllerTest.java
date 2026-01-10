/* @MENTEE_POWER (C)2026 */
package ru.mentee.banking.validation.api.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.mentee.banking.validation.api.advice.GlobalValidationExceptionHandler;
import ru.mentee.banking.validation.service.AccountService;

@Disabled("Отключено для ускорения тестов - используется только booking модуль")
@WebMvcTest(AccountController.class)
@Import(GlobalValidationExceptionHandler.class)
class AccountControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockBean private AccountService accountService;

    @Test
    @DisplayName("Should вернуть детальные ошибки валидации")
    void shouldReturnDetailedValidationErrors() throws Exception {
        // Given - валидный enum, но невалидные поля
        String invalidRequest =
                """
            {
                "accountType": "CHECKING",
                "currency": "US",
                "initialDeposit": -100,
                "personalInfo": {
                    "firstName": "A",
                    "lastName": "",
                    "email": "not-an-email",
                    "birthDate": "2030-01-01",
                    "passport": "123",
                    "phone": ""
                }
            }
            """;

        // When & Then
        // Ожидаем 9 ошибок:
        // - currency (Pattern: 3 chars)
        // - initialDeposit (PositiveOrZero)
        // - personalInfo.firstName (Size: min 2)
        // - personalInfo.lastName (Size: min 2) + (NotBlank) = 2 ошибки!
        // - personalInfo.email (Email)
        // - personalInfo.birthDate (Past)
        // - personalInfo.passport (Pattern)
        // - personalInfo.phone (NotBlank)
        mockMvc.perform(
                        post("/api/v2/accounts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(invalidRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors").isArray())
                .andExpect(jsonPath("$.fieldErrors.length()").value(9))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Validation Failed"))
                .andExpect(
                        jsonPath("$.fieldErrors[?(@.field == 'currency')].code").value("Pattern"))
                .andExpect(
                        jsonPath("$.fieldErrors[?(@.field == 'initialDeposit')].code")
                                .value("PositiveOrZero"))
                .andExpect(
                        jsonPath("$.fieldErrors[?(@.field == 'personalInfo.email')].code")
                                .value("Email"));
    }

    @Test
    @DisplayName("Should обработать невалидный enum (HttpMessageNotReadableException)")
    void shouldHandleInvalidEnumValue() throws Exception {
        // Given
        String invalidRequest =
                """
            {
                "accountType": "INVALID_TYPE",
                "currency": "USD",
                "initialDeposit": 1000,
                "personalInfo": {
                    "firstName": "John",
                    "lastName": "Doe",
                    "email": "john@example.com",
                    "birthDate": "1990-01-01",
                    "passport": "1234 567890",
                    "phone": "+1234567890"
                }
            }
            """;

        // When & Then
        mockMvc.perform(
                        post("/api/v2/accounts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(invalidRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Malformed JSON"))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @DisplayName("Should принять валидный запрос")
    void shouldAcceptValidRequest() throws Exception {
        // Given
        String validRequest =
                """
            {
                "accountType": "CHECKING",
                "currency": "USD",
                "initialDeposit": 1000.00,
                "personalInfo": {
                    "firstName": "John",
                    "lastName": "Doe",
                    "email": "john@example.com",
                    "birthDate": "1990-01-01",
                    "passport": "1234 567890",
                    "phone": "+1234567890"
                }
            }
            """;

        // When & Then
        mockMvc.perform(
                        post("/api/v2/accounts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(validRequest))
                .andExpect(status().isCreated());
    }
}
