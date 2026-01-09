/* @MENTEE_POWER (C)2026 */
package ru.mentee.banking.validation.validation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;
import ru.mentee.banking.validation.validation.annotation.IBAN;

@Component
public class IBANValidator implements ConstraintValidator<IBAN, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return true; // use @NotBlank separately
        }

        // Basic IBAN validation
        // Remove spaces
        String iban = value.replace(" ", "");

        // Check length (15-34 characters)
        if (iban.length() < 15 || iban.length() > 34) {
            return false;
        }

        // Check format: 2 letters + 2 digits + alphanumeric
        if (!iban.matches("^[A-Z]{2}\\d{2}[A-Z0-9]+$")) {
            return false;
        }

        // Luhn check (simplified)
        return checkIBANChecksum(iban);
    }

    private boolean checkIBANChecksum(String iban) {
        // Move first 4 chars to end
        String rearranged = iban.substring(4) + iban.substring(0, 4);

        // Replace letters with numbers (A=10, B=11, etc.)
        StringBuilder numeric = new StringBuilder();
        for (char ch : rearranged.toCharArray()) {
            if (Character.isLetter(ch)) {
                numeric.append(ch - 'A' + 10);
            } else {
                numeric.append(ch);
            }
        }

        // Check mod 97
        return mod97(numeric.toString()) == 1;
    }

    private int mod97(String number) {
        int remainder = 0;
        for (int i = 0; i < number.length(); i++) {
            int digit = Character.getNumericValue(number.charAt(i));
            remainder = (remainder * 10 + digit) % 97;
        }
        return remainder;
    }
}
