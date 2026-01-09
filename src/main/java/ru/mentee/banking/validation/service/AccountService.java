/* @MENTEE_POWER (C)2026 */
package ru.mentee.banking.validation.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.mentee.banking.validation.api.dto.AccountResponse;
import ru.mentee.banking.validation.api.dto.CreateAccountRequest;
import ru.mentee.banking.validation.domain.model.Account;

@Service("validationAccountService")
@Validated
@Slf4j
public class AccountService {

    @NotNull public AccountResponse createAccount(@Valid @NotNull CreateAccountRequest request) {
        log.info(
                "Creating account: type={}, currency={}",
                request.getAccountType(),
                request.getCurrency());

        // Business logic here
        Account account =
                Account.builder()
                        .id(UUID.randomUUID().toString())
                        .accountType(request.getAccountType())
                        .currency(request.getCurrency())
                        .balance(
                                request.getInitialDeposit() != null
                                        ? request.getInitialDeposit()
                                        : BigDecimal.ZERO)
                        .personalInfo(null) // mapper would handle this
                        .build();

        return AccountResponse.builder()
                .id(account.getId())
                .accountType(account.getAccountType())
                .currency(account.getCurrency())
                .balance(account.getBalance())
                .build();
    }
}
