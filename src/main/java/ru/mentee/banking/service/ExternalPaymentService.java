/* @MENTEE_POWER (C)2025 */
package ru.mentee.banking.service;

import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.mentee.banking.annotation.Auditable;
import ru.mentee.banking.annotation.RequiresRole;
import ru.mentee.banking.annotation.Retryable;
import ru.mentee.banking.api.dto.PaymentRequest;
import ru.mentee.banking.domain.model.Account;
import ru.mentee.banking.exception.TransientException;
import ru.mentee.banking.repository.AccountRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExternalPaymentService {

    private final AccountRepository accountRepository;
    private final PaymentGateway paymentGateway;

    @RequiresRole({"ADMIN", "USER"})
    @Auditable(action = "PROCESS_PAYMENT", logArgs = true, logResult = true)
    @Retryable(
            maxAttempts = 5,
            delay = 2000,
            retryOn = {TransientException.class})
    public PaymentResult processPayment(PaymentRequest request) {
        log.info(
                "Processing payment for account {} amount {}",
                request.getAccountId(),
                request.getAmount());

        Account account =
                accountRepository
                        .findById(request.getAccountId())
                        .orElseThrow(
                                () ->
                                        new RuntimeException(
                                                "Account not found: " + request.getAccountId()));

        if (account.getBalance().compareTo(request.getAmount()) < 0) {
            throw new RuntimeException("Insufficient funds");
        }

        BigDecimal newBalance = account.getBalance().subtract(request.getAmount());
        accountRepository.updateBalance(request.getAccountId(), newBalance);

        return paymentGateway.processPayment(request);
    }

    public static class PaymentResult {
        private String status;

        public PaymentResult() {}

        public PaymentResult(String status) {
            this.status = status;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }
}
