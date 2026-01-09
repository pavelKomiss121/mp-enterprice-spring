/* @MENTEE_POWER (C)2026 */
package ru.mentee.banking.validation.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.mentee.banking.validation.api.dto.TransferRequest;
import ru.mentee.banking.validation.api.dto.TransferResponse;

@Service("validationTransferService")
@Validated
@Slf4j
public class TransferService {

    @NotNull public TransferResponse executeTransfer(@Valid @NotNull TransferRequest request) {
        log.info(
                "Executing transfer: from={}, to={}, amount={}",
                request.getFromAccount(),
                request.getToAccount(),
                request.getAmount());

        // Business logic here
        // Check balance, process transfer, etc.

        return TransferResponse.builder()
                .transferId(UUID.randomUUID().toString())
                .fromAccount(request.getFromAccount())
                .toAccount(request.getToAccount())
                .amount(request.getAmount())
                .currency(request.getCurrency())
                .status("COMPLETED")
                .createdAt(LocalDateTime.now())
                .build();
    }
}
