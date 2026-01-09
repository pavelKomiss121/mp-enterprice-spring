/* @MENTEE_POWER (C)2025 */
package ru.mentee.banking.domain.model;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class Account {
    private String accountId;
    private BigDecimal balance;
    private String currency;
}
