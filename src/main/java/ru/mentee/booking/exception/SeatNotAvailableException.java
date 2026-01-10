/* @MENTEE_POWER (C)2026 */
package ru.mentee.booking.exception;

public class SeatNotAvailableException extends RuntimeException {
    public SeatNotAvailableException(Long seatId) {
        super("Seat is not available: " + seatId);
    }
}
