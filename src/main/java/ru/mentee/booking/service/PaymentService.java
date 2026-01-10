/* @MENTEE_POWER (C)2026 */
package ru.mentee.booking.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.mentee.booking.exception.PaymentProcessingException;

@Service
@Slf4j
public class PaymentService {

    @Transactional(
            propagation = Propagation.REQUIRES_NEW,
            rollbackFor = PaymentProcessingException.class)
    public void processPayment(Long bookingId, Long userId) throws PaymentProcessingException {
        log.info("Processing payment for booking: {}, user: {}", bookingId, userId);

        // Симуляция обработки платежа (всегда успешно для тестов)
        // В реальной системе здесь был бы вызов платежного шлюза
        log.info("Payment processed successfully for booking: {}", bookingId);
    }
}
