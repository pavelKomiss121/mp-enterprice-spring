/* @MENTEE_POWER (C)2025 */
package ru.mentee.banking.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mentee.banking.api.dto.BalanceDto;
import ru.mentee.banking.service.AccountService;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @GetMapping("/{accountId}/balance")
    public ResponseEntity<BalanceDto> getBalance(@PathVariable String accountId) {
        BalanceDto balance = accountService.getBalance(accountId);
        return ResponseEntity.ok(balance);
    }
}
