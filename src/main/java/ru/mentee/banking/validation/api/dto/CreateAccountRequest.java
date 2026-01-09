/* @MENTEE_POWER (C)2026 */
package ru.mentee.banking.validation.api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import lombok.*;
import ru.mentee.banking.validation.domain.model.AccountType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateAccountRequest {

    @NotNull(message = "{validation.account.accountType.notNull}") private AccountType accountType;

    @NotBlank(message = "{validation.account.currency.notBlank}")
    @Pattern(regexp = "^[A-Z]{3}$", message = "{validation.account.currency.pattern}")
    private String currency;

    @PositiveOrZero(message = "{validation.account.initialDeposit.positive}")
    private BigDecimal initialDeposit;

    @Valid
    @NotNull(message = "{validation.account.personalInfo.notNull}") private PersonalInfoDto personalInfo;
}
