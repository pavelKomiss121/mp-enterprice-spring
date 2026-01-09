/* @MENTEE_POWER (C)2025 */
package ru.mentee.banking.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Auditable {
    String action() default "";

    boolean logArgs() default true;

    boolean logResult() default true;
}
