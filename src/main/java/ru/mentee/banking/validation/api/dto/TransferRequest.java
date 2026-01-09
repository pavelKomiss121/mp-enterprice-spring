/* @MENTEE_POWER (C)2026 */
package ru.mentee.banking.validation.api.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import lombok.*;
import ru.mentee.banking.validation.domain.model.TransferUrgency;
import ru.mentee.banking.validation.validation.annotation.IBAN;
import ru.mentee.banking.validation.validation.annotation.ValidTransfer;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ValidTransfer
public class TransferRequest {

    @NotBlank(message = "{validation.transfer.fromAccount.notBlank}")
    @IBAN(message = "{validation.transfer.fromAccount.iban}")
    private String fromAccount;

    @NotBlank(message = "{validation.transfer.toAccount.notBlank}")
    @IBAN(message = "{validation.transfer.toAccount.iban}")
    private String toAccount;

    private String beneficiaryName;

    @NotNull(message = "{validation.transfer.amount.notNull}") @DecimalMin(value = "0.01", message = "{validation.transfer.amount.min}")
    @DecimalMax(value = "1000000", message = "{validation.transfer.amount.max}")
    private BigDecimal amount;

    @NotBlank(message = "{validation.transfer.currency.notBlank}")
    @Pattern(regexp = "^[A-Z]{3}$", message = "{validation.transfer.currency.pattern}")
    private String currency;

    @Size(max = 210, message = "{validation.transfer.purpose.size}")
    private String purpose;

    private TransferUrgency urgency;
}
