/* @MENTEE_POWER (C)2025 */
package ru.mentee.banking.exception;

public class TransientException extends RuntimeException {
    public TransientException() {
        super();
    }

    public TransientException(String message) {
        super(message);
    }
}
