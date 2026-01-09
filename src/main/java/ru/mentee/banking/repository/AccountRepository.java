/* @MENTEE_POWER (C)2025 */
package ru.mentee.banking.repository;

import java.math.BigDecimal;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import ru.mentee.banking.domain.model.Account;

@Repository
public class AccountRepository {
    public Optional<Account> findById(String accountId) {
        return Optional.of(
                Account.builder()
                        .accountId(accountId)
                        .balance(new BigDecimal("1000.00"))
                        .currency("USD")
                        .build());
    }

    public void updateBalance(String accountId, BigDecimal balance) {
        // Implementation
    }
}
