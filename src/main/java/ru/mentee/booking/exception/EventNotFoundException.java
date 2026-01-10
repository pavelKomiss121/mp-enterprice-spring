/* @MENTEE_POWER (C)2026 */
package ru.mentee.booking.exception;

public class EventNotFoundException extends RuntimeException {
    public EventNotFoundException(Long eventId) {
        super("Event not found with id: " + eventId);
    }
}
