/* @MENTEE_POWER (C)2026 */
package ru.mentee.banking.validation.validation.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;
import ru.mentee.banking.validation.validation.validator.TransferValidator;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = TransferValidator.class)
@Documented
public @interface ValidTransfer {
    String message() default "{validation.transfer.invalid}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
