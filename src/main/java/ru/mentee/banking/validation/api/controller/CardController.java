/* @MENTEE_POWER (C)2026 */
package ru.mentee.banking.validation.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mentee.banking.validation.api.dto.CardOrderRequest;
import ru.mentee.banking.validation.service.CardService;

@RestController("validationCardController")
@RequestMapping("/api/v2/cards")
@RequiredArgsConstructor
@Slf4j
public class CardController {

    private final CardService cardService;

    @PostMapping
    public ResponseEntity<Void> orderCard(@RequestBody @Valid CardOrderRequest request) {
        log.info("POST /api/cards - ordering card");

        cardService.orderCard(request);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
