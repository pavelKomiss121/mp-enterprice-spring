/* @MENTEE_POWER (C)2025 */
package ru.mentee.banking.api.dto;

import java.time.LocalDateTime;
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
public class TransferResult {
    private String transactionId;
    private String status;
    private LocalDateTime timestamp;
}
