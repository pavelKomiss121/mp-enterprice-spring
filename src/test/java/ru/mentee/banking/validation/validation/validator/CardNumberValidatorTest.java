/* @MENTEE_POWER (C)2026 */
package ru.mentee.banking.validation.validation.validator;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class CardNumberValidatorTest {

    private CardNumberValidator validator;

    @BeforeEach
    void setUp() {
        validator = new CardNumberValidator();
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                "4532015112830366", // Visa
                "5425233430109903", // Mastercard
                "374245455400126", // Amex
                "6011000991300009" // Discover
            })
    @DisplayName("Should принять валидные номера карт")
    void shouldAcceptValidCardNumbers(String cardNumber) {
        assertThat(validator.isValid(cardNumber, null)).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {"1234567890123456", "0000000000000000", "abc"})
    @DisplayName("Should отклонить некорректные номера карт")
    void shouldRejectInvalidCardNumbers(String cardNumber) {
        assertThat(validator.isValid(cardNumber, null)).isFalse();
    }
}
