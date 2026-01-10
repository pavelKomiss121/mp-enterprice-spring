/* @MENTEE_POWER (C)2026 */
package ru.mentee.booking.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mentee.booking.api.dto.BookingRequest;
import ru.mentee.booking.api.dto.BookingResponse;
import ru.mentee.booking.domain.model.Booking;
import ru.mentee.booking.exception.EventNotFoundException;
import ru.mentee.booking.exception.SeatNotAvailableException;
import ru.mentee.booking.exception.SeatNotFoundException;
import ru.mentee.booking.service.BookingService;

@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(
            @Valid @RequestBody BookingRequest request) {
        log.info("Received booking request: {}", request);

        Booking booking =
                bookingService.bookSeat(
                        request.getEventId(), request.getSeatId(), request.getUserId());

        BookingResponse response =
                BookingResponse.builder()
                        .bookingId(booking.getId())
                        .status(booking.getStatus())
                        .seatNumber(booking.getSeat().getSeatNumber())
                        .eventName(booking.getEvent().getName())
                        .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @ExceptionHandler(EventNotFoundException.class)
    public ResponseEntity<String> handleEventNotFound(EventNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(SeatNotFoundException.class)
    public ResponseEntity<String> handleSeatNotFound(SeatNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(SeatNotAvailableException.class)
    public ResponseEntity<String> handleSeatNotAvailable(SeatNotAvailableException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception ex) {
        log.error("Unexpected error", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Internal server error: " + ex.getMessage());
    }
}
