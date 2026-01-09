/* @MENTEE_POWER (C)2025 */
package ru.mentee.banking.api.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentRequest {
    @NotBlank(message = "Account ID is required")
    private String accountId;

    @NotBlank(message = "Payment details are required")
    private String paymentDetails;

    @NotNull(message = "Amount is required") @DecimalMin(value = "0.01", message = "Amount must be at least 0.01")
    private BigDecimal amount;
}
