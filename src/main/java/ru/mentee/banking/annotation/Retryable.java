/* @MENTEE_POWER (C)2025 */
package ru.mentee.banking.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Retryable {
    int maxAttempts() default 3;

    long delay() default 1000;

    Class<? extends Exception>[] retryOn() default {Exception.class};
}
