/* @MENTEE_POWER (C)2026 */
package ru.mentee.booking.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class AuditService {

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logBookingAttempt(Long eventId, Long seatId, Long userId, String result) {
        log.info("AUDIT: Event={}, Seat={}, User={}, Result={}", eventId, seatId, userId, result);
        // В реальном проекте: auditRepository.save(new AuditLog(...))
    }
}
