/* @MENTEE_POWER (C)2025 */
package ru.mentee.banking.api.controller;

import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.mentee.banking.api.dto.TransferRequest;
import ru.mentee.banking.api.dto.TransferResult;
import ru.mentee.banking.service.TransferService;

@RestController
@RequestMapping("/api/transfers")
@RequiredArgsConstructor
public class TransferController {

    private final TransferService transferService;

    @PostMapping
    public ResponseEntity<TransferResult> transfer(@Valid @RequestBody TransferRequest request) {
        TransferResult result = transferService.transfer(request);
        URI location =
                ServletUriComponentsBuilder.fromCurrentRequest()
                        .path("/{id}")
                        .buildAndExpand(result.getTransactionId())
                        .toUri();
        return ResponseEntity.status(HttpStatus.CREATED).location(location).body(result);
    }
}
