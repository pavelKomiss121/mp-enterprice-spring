/* @MENTEE_POWER (C)2026 */
package ru.mentee.banking.validation.validation.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;
import ru.mentee.banking.validation.validation.validator.CardNumberValidator;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CardNumberValidator.class)
@Documented
public @interface CardNumber {
    String message() default "{validation.cardNumber.invalid}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
