/* @MENTEE_POWER (C)2025 */
package ru.mentee.banking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mentee.banking.annotation.Auditable;
import ru.mentee.banking.annotation.Cacheable;
import ru.mentee.banking.annotation.RequiresRole;
import ru.mentee.banking.api.dto.BalanceDto;
import ru.mentee.banking.domain.model.Account;
import ru.mentee.banking.repository.AccountRepository;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    @RequiresRole({"ADMIN", "USER"})
    @Auditable(action = "GET_BALANCE", logArgs = true, logResult = false)
    @Cacheable(cacheName = "balance", ttl = 60)
    public BalanceDto getBalance(String accountId) {
        Account account =
                accountRepository
                        .findById(accountId)
                        .orElseThrow(() -> new RuntimeException("Account not found: " + accountId));

        return BalanceDto.builder()
                .accountId(account.getAccountId())
                .amount(account.getBalance())
                .currency(account.getCurrency())
                .build();
    }
}
