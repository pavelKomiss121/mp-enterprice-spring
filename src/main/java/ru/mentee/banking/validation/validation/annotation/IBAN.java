/* @MENTEE_POWER (C)2026 */
package ru.mentee.banking.validation.validation.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;
import ru.mentee.banking.validation.validation.validator.IBANValidator;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = IBANValidator.class)
@Documented
public @interface IBAN {
    String message() default "{validation.iban.invalid}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
