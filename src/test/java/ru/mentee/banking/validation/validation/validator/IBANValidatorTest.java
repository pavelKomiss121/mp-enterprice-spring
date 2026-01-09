/* @MENTEE_POWER (C)2026 */
package ru.mentee.banking.validation.validation.validator;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class IBANValidatorTest {

    private IBANValidator validator;

    @BeforeEach
    void setUp() {
        validator = new IBANValidator();
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                "DE89370400440532013000",
                "GB82WEST12345698765432",
                "FR1420041010050500013M02606",
                "IT60X0542811101000000123456"
            })
    @DisplayName("Should принять валидные IBAN")
    void shouldAcceptValidIBANs(String iban) {
        assertThat(validator.isValid(iban, null)).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {"INVALID", "123456", "AA12"})
    @DisplayName("Should отклонить некорректные IBAN")
    void shouldRejectInvalidIBANs(String iban) {
        assertThat(validator.isValid(iban, null)).isFalse();
    }

    @Test
    @DisplayName("Should принять null (используйте @NotBlank отдельно)")
    void shouldAcceptNull() {
        assertThat(validator.isValid(null, null)).isTrue();
    }

    @Test
    @DisplayName("Should принять пустую строку (используйте @NotBlank отдельно)")
    void shouldAcceptBlank() {
        assertThat(validator.isValid("", null)).isTrue();
        assertThat(validator.isValid("   ", null)).isTrue();
    }
}
