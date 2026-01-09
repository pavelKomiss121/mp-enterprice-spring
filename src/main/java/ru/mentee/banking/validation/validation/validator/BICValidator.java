/* @MENTEE_POWER (C)2026 */
package ru.mentee.banking.validation.validation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;
import ru.mentee.banking.validation.validation.annotation.BIC;

@Component
public class BICValidator implements ConstraintValidator<BIC, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return true;
        }

        // BIC/SWIFT code: 8 or 11 alphanumeric characters
        // Format: AAAA BB CC DDD
        // AAAA - bank code (4 letters)
        // BB - country code (2 letters)
        // CC - location code (2 alphanumeric)
        // DDD - branch code (3 alphanumeric, optional)

        if (value.length() != 8 && value.length() != 11) {
            return false;
        }

        return value.matches("^[A-Z]{4}[A-Z]{2}[A-Z0-9]{2}([A-Z0-9]{3})?$");
    }
}
