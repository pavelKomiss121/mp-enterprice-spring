/* @MENTEE_POWER (C)2025 */
package ru.mentee.banking.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import ru.mentee.banking.domain.model.AuditEntry;

@Service
public class AuditService {
    public void save(AuditEntry entry) {
        // Implementation
    }

    public List<AuditEntry> findByUserId(String userId) {
        return new ArrayList<>();
    }

    public List<AuditEntry> findByDateRange(LocalDateTime from, LocalDateTime to) {
        return new ArrayList<>();
    }

    public List<AuditEntry> findAll() {
        return new ArrayList<>();
    }
}
