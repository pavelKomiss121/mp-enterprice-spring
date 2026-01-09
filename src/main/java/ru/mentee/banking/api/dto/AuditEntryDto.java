/* @MENTEE_POWER (C)2025 */
package ru.mentee.banking.api.dto;

import java.time.LocalDateTime;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditEntryDto {
    private String id;
    private String userId;
    private String operation;
    private LocalDateTime timestamp;
    private String status;
    private Map<String, Object> details;
}
