/* @MENTEE_POWER (C)2026 */
package ru.mentee.banking.validation.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mentee.banking.validation.api.dto.TransferRequest;
import ru.mentee.banking.validation.api.dto.TransferResponse;
import ru.mentee.banking.validation.service.TransferService;

@RestController("validationTransferController")
@RequestMapping("/api/v2/transfers")
@RequiredArgsConstructor
@Slf4j
public class TransferController {

    private final TransferService transferService;

    @PostMapping
    public ResponseEntity<TransferResponse> executeTransfer(
            @RequestBody @Valid TransferRequest request) {
        log.info("POST /api/transfers - executing transfer");

        TransferResponse response = transferService.executeTransfer(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
