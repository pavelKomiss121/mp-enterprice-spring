/* @MENTEE_POWER (C)2026 */
package ru.mentee.banking.validation.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import ru.mentee.banking.validation.validation.annotation.CardNumber;
import ru.mentee.banking.validation.validation.annotation.IBAN;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardOrderRequest {

    @NotBlank(message = "{validation.card.accountNumber.notBlank}")
    @IBAN(message = "{validation.card.accountNumber.iban}")
    private String accountNumber;

    @CardNumber(message = "{validation.card.cardNumber.invalid}")
    private String cardNumber;

    private String cardholderName;
}
