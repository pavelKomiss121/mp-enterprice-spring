/* @MENTEE_POWER (C)2025 */
package ru.mentee.banking.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.mentee.banking.annotation.Auditable;
import ru.mentee.banking.annotation.RequiresRole;
import ru.mentee.banking.api.dto.TransferRequest;
import ru.mentee.banking.api.dto.TransferResult;
import ru.mentee.banking.domain.model.Account;
import ru.mentee.banking.repository.AccountRepository;

@Slf4j
@Service
public class TransferService {

    private final AccountRepository accountRepository;

    @org.springframework.beans.factory.annotation.Autowired
    @org.springframework.context.annotation.Lazy
    private TransferService self; // Self-proxy для решения self-invocation

    public TransferService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @RequiresRole({"ADMIN", "USER"})
    @Auditable(action = "TRANSFER", logArgs = true, logResult = true)
    public TransferResult transfer(TransferRequest request) {
        log.info(
                "Processing transfer from {} to {} amount {}",
                request.getFromAccount(),
                request.getToAccount(),
                request.getAmount());

        Account fromAccount =
                accountRepository
                        .findById(request.getFromAccount())
                        .orElseThrow(
                                () ->
                                        new RuntimeException(
                                                "From account not found: "
                                                        + request.getFromAccount()));

        Account toAccount =
                accountRepository
                        .findById(request.getToAccount())
                        .orElseThrow(
                                () ->
                                        new RuntimeException(
                                                "To account not found: " + request.getToAccount()));

        if (fromAccount.getBalance().compareTo(request.getAmount()) < 0) {
            throw new RuntimeException("Insufficient funds");
        }

        BigDecimal fromNewBalance = fromAccount.getBalance().subtract(request.getAmount());
        BigDecimal toNewBalance = toAccount.getBalance().add(request.getAmount());

        accountRepository.updateBalance(request.getFromAccount(), fromNewBalance);
        accountRepository.updateBalance(request.getToAccount(), toNewBalance);

        String transactionId = UUID.randomUUID().toString();
        log.info("Transfer completed. Transaction ID: {}", transactionId);

        return TransferResult.builder()
                .transactionId(transactionId)
                .status("SUCCESS")
                .timestamp(LocalDateTime.now())
                .build();
    }
}
