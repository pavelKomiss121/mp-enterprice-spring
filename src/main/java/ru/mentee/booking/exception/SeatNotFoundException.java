/* @MENTEE_POWER (C)2026 */
package ru.mentee.booking.exception;

public class SeatNotFoundException extends RuntimeException {
    public SeatNotFoundException(Long seatId) {
        super("Seat not found with id: " + seatId);
    }
}
