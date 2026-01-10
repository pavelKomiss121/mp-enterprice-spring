/* @MENTEE_POWER (C)2026 */
package ru.mentee.booking.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.mentee.booking.domain.model.Event;
import ru.mentee.booking.domain.model.Seat;
import ru.mentee.booking.domain.model.SeatStatus;
import ru.mentee.booking.domain.repository.BookingRepository;
import ru.mentee.booking.domain.repository.EventRepository;
import ru.mentee.booking.domain.repository.SeatRepository;
import ru.mentee.booking.exception.SeatNotAvailableException;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
class BookingServiceIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:15")
                    .withDatabaseName("testdb")
                    .withUsername("test")
                    .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
        registry.add(
                "spring.jpa.properties.hibernate.dialect",
                () -> "org.hibernate.dialect.PostgreSQLDialect");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "validate");
        registry.add("spring.liquibase.enabled", () -> "true");
        registry.add(
                "spring.liquibase.change-log",
                () -> "classpath:db/migration/booking-changelog.xml");
    }

    @Autowired private BookingService bookingService;

    @Autowired private EventRepository eventRepository;

    @Autowired private SeatRepository seatRepository;

    @Autowired private BookingRepository bookingRepository;

    @BeforeEach
    void setUp() {
        // Очистка данных перед каждым тестом
        // Удаляем в правильном порядке: сначала зависимые таблицы, потом родительские
        bookingRepository.deleteAll();
        seatRepository.deleteAll();
        eventRepository.deleteAll();
    }

    private Event createEvent(int totalSeats) {
        return eventRepository.save(
                Event.builder().name("Test Event").totalSeats(totalSeats).bookedSeats(0).build());
    }

    private Seat createSeat(Event event, String seatNumber) {
        return seatRepository.save(
                Seat.builder()
                        .event(event)
                        .seatNumber(seatNumber)
                        .status(SeatStatus.AVAILABLE)
                        .build());
    }

    @Test
    @DisplayName("Should откатить транзакцию при нехватке мест")
    void shouldRollbackOnNoSeatsAvailable() {
        // Given
        Event event = createEvent(1); // 1 место
        Seat seat = createSeat(event, "A1");

        // Бронируем первое место
        bookingService.bookSeat(event.getId(), seat.getId(), 1L);

        // When & Then
        // Пытаемся забронировать то же место
        assertThrows(
                SeatNotAvailableException.class,
                () -> {
                    bookingService.bookSeat(event.getId(), seat.getId(), 2L);
                });

        // Проверяем, что состояние не изменилось
        Event eventAfter = eventRepository.findById(event.getId()).orElseThrow();
        assertThat(eventAfter.getBookedSeats()).isEqualTo(1);

        Seat seatAfter = seatRepository.findById(seat.getId()).orElseThrow();
        assertThat(seatAfter.getStatus()).isEqualTo(SeatStatus.BOOKED);
    }

    @Test
    @DisplayName("Should обработать конкурентное бронирование с optimistic lock")
    void shouldHandleConcurrentBookingWithOptimisticLock() throws InterruptedException {
        // Given
        Event event = createEvent(100);
        CountDownLatch latch = new CountDownLatch(2);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger optimisticLockFailureCount = new AtomicInteger(0);

        Runnable task =
                () -> {
                    try {
                        bookingService.incrementEventBookingCount(event.getId());
                        successCount.incrementAndGet();
                    } catch (org.springframework.orm.ObjectOptimisticLockingFailureException e) {
                        optimisticLockFailureCount.incrementAndGet();
                    } catch (Exception e) {
                        // Другие исключения не ожидаются
                    } finally {
                        latch.countDown();
                    }
                };

        // When
        new Thread(task).start();
        new Thread(task).start();

        boolean completed = latch.await(10, TimeUnit.SECONDS);
        assertThat(completed).as("Both threads should complete within timeout").isTrue();

        // Then
        Event finalEvent = eventRepository.findById(event.getId()).orElseThrow();

        // Основная проверка: оптимистическая блокировка должна работать
        // Результат зависит от таймингов:
        // - Если оптимистическая блокировка сработала: 1 успех, 1
        // OptimisticLockingFailureException, bookedSeats = 1
        // - Если оба потока успели до конфликта (редко): 2 успеха, bookedSeats = 2

        // Проверяем, что итоговое значение корректно (не больше 2, не меньше 1)
        assertThat(finalEvent.getBookedSeats())
                .as(
                        "Booked seats should be 1 (if optimistic lock worked) or 2 (if both threads"
                                + " succeeded)")
                .isBetween(1, 2);

        // Проверяем, что оба потока завершились
        assertThat(successCount.get() + optimisticLockFailureCount.get())
                .as("Both threads should complete (either success or optimistic lock failure)")
                .isEqualTo(2);

        // Если оптимистическая блокировка сработала, должен быть хотя бы один
        // OptimisticLockingFailureException
        // Это основная проверка - оптимистическая блокировка должна предотвращать одновременные
        // обновления
        if (optimisticLockFailureCount.get() > 0) {
            // Оптимистическая блокировка сработала - отлично!
            assertThat(finalEvent.getBookedSeats()).isEqualTo(1);
            assertThat(successCount.get()).isEqualTo(1);
        } else {
            // Оба потока успели до конфликта (редкий случай в быстром CI/CD) - это тоже валидный
            // результат
            // Но bookedSeats должно быть 2
            assertThat(finalEvent.getBookedSeats()).isEqualTo(2);
            assertThat(successCount.get()).isEqualTo(2);
        }
    }

    @Test
    @DisplayName("Should успешно забронировать место")
    void shouldBookSeatSuccessfully() {
        // Given
        Event event = createEvent(10);
        Seat seat = createSeat(event, "A1");

        // When
        var booking = bookingService.bookSeat(event.getId(), seat.getId(), 123L);

        // Then
        assertThat(booking).isNotNull();
        assertThat(booking.getId()).isNotNull();
        assertThat(booking.getUserId()).isEqualTo(123L);

        // Проверяем, что место забронировано
        Seat updatedSeat = seatRepository.findById(seat.getId()).orElseThrow();
        assertThat(updatedSeat.getStatus()).isEqualTo(SeatStatus.BOOKED);

        // Проверяем, что счётчик увеличился
        Event updatedEvent = eventRepository.findById(event.getId()).orElseThrow();
        assertThat(updatedEvent.getBookedSeats()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should использовать пессимистическую блокировку для места")
    void shouldUsePessimisticLockForSeat() throws InterruptedException {
        // Given
        Event event = createEvent(10);
        Seat seat = createSeat(event, "A1");
        CountDownLatch latch = new CountDownLatch(2);
        AtomicInteger successCount = new AtomicInteger(0);

        Runnable task =
                () -> {
                    try {
                        bookingService.bookSeat(event.getId(), seat.getId(), 1L);
                        successCount.incrementAndGet();
                    } catch (SeatNotAvailableException e) {
                        // Ожидаемое исключение для второго потока
                    } finally {
                        latch.countDown();
                    }
                };

        // When
        Thread t1 = new Thread(task);
        Thread t2 = new Thread(task);
        t1.start();
        t2.start();

        latch.await(10, TimeUnit.SECONDS);

        // Then
        // Только один поток должен успешно забронировать
        assertThat(successCount.get()).isEqualTo(1);

        Seat finalSeat = seatRepository.findById(seat.getId()).orElseThrow();
        assertThat(finalSeat.getStatus()).isEqualTo(SeatStatus.BOOKED);
    }
}
