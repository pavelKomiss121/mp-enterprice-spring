/* @MENTEE_POWER (C)2026 */
package ru.mentee.banking.validation.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.mentee.banking.validation.api.dto.CardOrderRequest;

@Service("validationCardService")
@Validated
@Slf4j
public class CardService {

    public void orderCard(@Valid @NotNull CardOrderRequest request) {
        log.info("Ordering card for account: {}", request.getAccountNumber());

        // Business logic here
    }
}
