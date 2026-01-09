/* @MENTEE_POWER (C)2026 */
package ru.mentee.banking.validation.api.dto;

import java.math.BigDecimal;
import lombok.*;
import ru.mentee.banking.validation.domain.model.AccountType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountResponse {
    private String id;
    private AccountType accountType;
    private String currency;
    private BigDecimal balance;
}
