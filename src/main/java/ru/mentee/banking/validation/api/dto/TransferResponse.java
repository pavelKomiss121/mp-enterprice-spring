/* @MENTEE_POWER (C)2026 */
package ru.mentee.banking.validation.api.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransferResponse {
    private String transferId;
    private String fromAccount;
    private String toAccount;
    private BigDecimal amount;
    private String currency;
    private String status;
    private LocalDateTime createdAt;
}
