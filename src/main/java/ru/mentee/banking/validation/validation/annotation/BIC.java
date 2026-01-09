/* @MENTEE_POWER (C)2026 */
package ru.mentee.banking.validation.validation.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;
import ru.mentee.banking.validation.validation.validator.BICValidator;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = BICValidator.class)
@Documented
public @interface BIC {
    String message() default "{validation.bic.invalid}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
