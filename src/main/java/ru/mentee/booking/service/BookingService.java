/* @MENTEE_POWER (C)2026 */
package ru.mentee.booking.service;

import java.time.Instant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.mentee.booking.domain.model.*;
import ru.mentee.booking.domain.repository.BookingRepository;
import ru.mentee.booking.domain.repository.EventRepository;
import ru.mentee.booking.domain.repository.SeatRepository;
import ru.mentee.booking.exception.EventNotFoundException;
import ru.mentee.booking.exception.PaymentProcessingException;
import ru.mentee.booking.exception.SeatNotAvailableException;
import ru.mentee.booking.exception.SeatNotFoundException;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingService {

    private final EventRepository eventRepository;
    private final SeatRepository seatRepository;
    private final BookingRepository bookingRepository;
    private final AuditService auditService;
    private final PaymentService paymentService;

    // Self-injection для решения self-invocation problem
    // @Lazy - отложенная инициализация, чтобы избежать циклической зависимости
    @Autowired @Lazy private BookingService self;

    @Transactional(
            isolation = Isolation.READ_COMMITTED,
            rollbackFor = {SeatNotAvailableException.class, PaymentProcessingException.class},
            timeout = 10)
    public Booking bookSeat(Long eventId, Long seatId, Long userId) {
        log.info("Booking attempt: eventId={}, seatId={}, userId={}", eventId, seatId, userId);

        // 1. Проверка существования события
        Event event =
                eventRepository
                        .findById(eventId)
                        .orElseThrow(() -> new EventNotFoundException(eventId));

        // 2. Пессимистическая блокировка места (SELECT ... FOR UPDATE)
        Seat seat =
                seatRepository
                        .findByIdForUpdate(seatId)
                        .orElseThrow(() -> new SeatNotFoundException(seatId));

        // 3. Проверка доступности места
        if (seat.getStatus() != SeatStatus.AVAILABLE) {
            throw new SeatNotAvailableException(seatId);
        }

        // 4. Бронирование места
        seat.setStatus(SeatStatus.BOOKED);
        seatRepository.save(seat);

        // 5. Увеличение счётчика забронированных мест (с оптимистической блокировкой)
        self.incrementEventBookingCount(eventId);

        // 6. Создание записи бронирования
        Booking booking =
                Booking.builder()
                        .event(event)
                        .seat(seat)
                        .userId(userId)
                        .status(BookingStatus.PENDING_PAYMENT)
                        .createdAt(Instant.now())
                        .build();

        booking = bookingRepository.save(booking);

        // 7. Обработка платежа (REQUIRES_NEW - независимая транзакция)
        try {
            paymentService.processPayment(booking.getId(), userId);
            booking.setStatus(BookingStatus.CONFIRMED);
            booking = bookingRepository.save(booking);
        } catch (PaymentProcessingException e) {
            log.warn("Payment failed for booking: {}", booking.getId());
            // Откатится вся транзакция (rollbackFor)
            throw new RuntimeException("Payment processing failed", e);
        }

        // 8. Аудит (REQUIRES_NEW - сохранится даже при откате основной транзакции)
        try {
            auditService.logBookingAttempt(eventId, seatId, userId, "SUCCESS");
        } catch (Exception e) {
            log.error("Audit logging failed", e);
            // Не откатываем основную транзакцию
        }

        return booking;
    }

    @Transactional(
            propagation = org.springframework.transaction.annotation.Propagation.REQUIRED,
            isolation = Isolation.READ_COMMITTED)
    public void incrementEventBookingCount(Long eventId) {
        Event event =
                eventRepository
                        .findById(eventId)
                        .orElseThrow(() -> new EventNotFoundException(eventId));

        event.setBookedSeats(event.getBookedSeats() + 1);
        try {
            eventRepository.save(event);
            log.info("Event {} booked seats incremented to {}", eventId, event.getBookedSeats());
        } catch (org.springframework.orm.ObjectOptimisticLockingFailureException e) {
            log.warn(
                    "Optimistic locking failure for event {}. Another transaction modified the"
                            + " event.",
                    eventId);
            throw e; // Перебрасываем для теста
        }
    }
}
