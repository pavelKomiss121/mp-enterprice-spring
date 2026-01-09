/* @MENTEE_POWER (C)2025 */
package ru.mentee.banking.exception;

public class AccessDeniedException extends RuntimeException {
    public AccessDeniedException(String message) {
        super(message);
    }
}
