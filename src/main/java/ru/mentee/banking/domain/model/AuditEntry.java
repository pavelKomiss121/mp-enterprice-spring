/* @MENTEE_POWER (C)2025 */
package ru.mentee.banking.domain.model;

import java.time.LocalDateTime;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuditEntry {
    private String id;
    private String userId;
    private String operation;
    private LocalDateTime timestamp;
    private String parameters;
    private String status;
    private String result;
    private String error;
    private Map<String, Object> details;
}
