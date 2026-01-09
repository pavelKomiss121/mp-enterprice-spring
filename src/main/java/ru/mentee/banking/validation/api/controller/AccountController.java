/* @MENTEE_POWER (C)2026 */
package ru.mentee.banking.validation.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mentee.banking.validation.api.dto.AccountResponse;
import ru.mentee.banking.validation.api.dto.CreateAccountRequest;
import ru.mentee.banking.validation.service.AccountService;

@RestController("validationAccountController")
@RequestMapping("/api/v2/accounts")
@RequiredArgsConstructor
@Slf4j
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(
            @RequestBody @Valid CreateAccountRequest request) {
        log.info("POST /api/accounts - creating account");

        AccountResponse response = accountService.createAccount(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
