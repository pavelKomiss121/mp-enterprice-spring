/* @MENTEE_POWER (C)2025 */
package ru.mentee.banking.api.dto;

import java.math.BigDecimal;
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
public class BalanceDto {
    private String accountId;
    private BigDecimal amount;
    private String currency;
}
