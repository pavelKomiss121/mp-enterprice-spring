/* @MENTEE_POWER (C)2026 */
package ru.mentee.banking.validation.domain.model;

import java.math.BigDecimal;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Account {
    private String id;
    private AccountType accountType;
    private String currency;
    private BigDecimal balance;
    private PersonalInfo personalInfo;
}
