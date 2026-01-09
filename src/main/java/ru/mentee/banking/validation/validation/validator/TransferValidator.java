/* @MENTEE_POWER (C)2026 */
package ru.mentee.banking.validation.validation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;
import ru.mentee.banking.validation.api.dto.TransferRequest;
import ru.mentee.banking.validation.validation.annotation.ValidTransfer;

@Component
public class TransferValidator implements ConstraintValidator<ValidTransfer, TransferRequest> {

    @Override
    public boolean isValid(TransferRequest request, ConstraintValidatorContext context) {
        if (request == null) {
            return true;
        }

        boolean valid = true;

        // Cross-field validation: fromAccount != toAccount
        if (request.getFromAccount() != null
                && request.getToAccount() != null
                && request.getFromAccount().equals(request.getToAccount())) {

            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("{validation.transfer.sameAccount}")
                    .addPropertyNode("toAccount")
                    .addConstraintViolation();
            valid = false;
        }

        return valid;
    }
}
