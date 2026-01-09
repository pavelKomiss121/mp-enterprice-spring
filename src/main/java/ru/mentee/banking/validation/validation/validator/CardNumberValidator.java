/* @MENTEE_POWER (C)2026 */
package ru.mentee.banking.validation.validation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;
import ru.mentee.banking.validation.validation.annotation.CardNumber;

@Component
public class CardNumberValidator implements ConstraintValidator<CardNumber, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return true;
        }

        // Remove spaces and dashes
        String cardNumber = value.replaceAll("[\\s-]", "");

        // Check if only digits
        if (!cardNumber.matches("\\d+")) {
            return false;
        }

        // Check length (13-19 digits)
        if (cardNumber.length() < 13 || cardNumber.length() > 19) {
            return false;
        }

        // Reject cards with all same digits (e.g., 0000000000000000)
        if (cardNumber.matches("(\\d)\\1+")) {
            return false;
        }

        // Luhn algorithm
        return luhnCheck(cardNumber);
    }

    private boolean luhnCheck(String cardNumber) {
        int sum = 0;
        boolean alternate = false;

        // Loop from right to left
        for (int i = cardNumber.length() - 1; i >= 0; i--) {
            int digit = Character.getNumericValue(cardNumber.charAt(i));

            if (alternate) {
                digit *= 2;
                if (digit > 9) {
                    digit -= 9;
                }
            }

            sum += digit;
            alternate = !alternate;
        }

        return sum % 10 == 0;
    }
}
