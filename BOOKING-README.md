# MP-198: Booking Service - Транзакции в Spring

Система бронирования билетов с управлением транзакциями.

## Запуск

### 1. Запустите PostgreSQL

```bash
docker run --name booking-postgres \
  -e POSTGRES_DB=booking_db \
  -e POSTGRES_USER=booking_user \
  -e POSTGRES_PASSWORD=booking_password \
  -p 5432:5432 \
  -d postgres:15
```

### 2. Запустите приложение

```bash
cd D:\projects\java\mp-enterprice-spring
.\gradlew.bat bootRun --args='--spring.profiles.active=booking'
```

Приложение запустится на `http://localhost:8080`

## Тестирование API

### Создание бронирования

```bash
curl -X POST http://localhost:8080/api/v1/bookings \
  -H "Content-Type: application/json" \
  -d '{
    "eventId": 1,
    "seatId": 1,
    "userId": 123
  }'
```

### Ожидаемый ответ

```json
{
  "bookingId": 1,
  "status": "CONFIRMED",
  "seatNumber": "A1",
  "eventName": "Spring Conference 2026"
}
```

## Запуск тестов

```bash
.\gradlew.bat test --tests "ru.mentee.booking.service.BookingServiceIntegrationTest"
```

## Что реализовано

### ✅ Транзакции
- `@Transactional` с различными propagation levels
- REQUIRED для основной логики
- REQUIRES_NEW для аудита и платежей
- Isolation: READ_COMMITTED

### ✅ Блокировки
- **Оптимистическая** блокировка для Event (`@Version`)
- **Пессимистическая** блокировка для Seat (`SELECT ... FOR UPDATE`)

### ✅ Rollback Rules
- `rollbackFor`: SeatNotAvailableException, PaymentProcessingException
- Откат при RuntimeException (по умолчанию)

### ✅ Self-Invocation Problem
- Решено через `@Autowired private BookingService self`

### ✅ Тесты
- Откат транзакции при нехватке мест
- Конкурентное бронирование с optimistic lock
- Пессимистическая блокировка мест
- Testcontainers с PostgreSQL

## Схема базы данных

```
events
├── id (PK)
├── name
├── total_seats
├── booked_seats
└── version (для optimistic lock)

seats
├── id (PK)
├── event_id (FK)
├── seat_number
└── status (AVAILABLE/BOOKED/LOCKED)

bookings
├── id (PK)
├── event_id (FK)
├── user_id
├── seat_id (FK)
├── status (CONFIRMED/PENDING_PAYMENT)
└── created_at
```

## Тестовые данные

После запуска доступны:
- 3 события (Spring Conference 2026, Java Meetup, DevOps Summit)
- 8 мест для бронирования

