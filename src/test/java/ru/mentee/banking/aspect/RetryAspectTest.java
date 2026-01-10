/* @MENTEE_POWER (C)2025 */
package ru.mentee.banking.aspect;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.TestPropertySource;
import ru.mentee.banking.api.dto.PaymentRequest;
import ru.mentee.banking.exception.TransientException;
import ru.mentee.banking.service.ExternalPaymentService;
import ru.mentee.banking.service.PaymentGateway;
import ru.mentee.banking.service.SecurityContext;

@Disabled("Отключено для ускорения тестов - используется только booking модуль")
@SpringBootTest(classes = ru.mentee.banking.BankingApplication.class)
@TestPropertySource(
        properties = {
            "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration"
        })
class RetryAspectTest {

    @Autowired private ExternalPaymentService paymentService;

    @MockBean private PaymentGateway paymentGateway;

    @SpyBean private SecurityContext securityContext;

    @BeforeEach
    void setUp() {
        securityContext.getRoles().clear();
        securityContext.getRoles().add("ADMIN");
    }

    @Test
    @DisplayName("Should повторить попытку при временной ошибке")
    void shouldRetryOnTransientError() {
        // Given
        PaymentRequest request = new PaymentRequest();
        request.setAccountId("ACC001");
        request.setAmount(new java.math.BigDecimal("100.00"));
        request.setPaymentDetails("Test payment");

        when(paymentGateway.processPayment(any()))
                .thenThrow(new TransientException())
                .thenThrow(new TransientException())
                .thenReturn(new ExternalPaymentService.PaymentResult("SUCCESS"));

        // When
        ExternalPaymentService.PaymentResult result = paymentService.processPayment(request);

        // Then
        assertThat(result.getStatus()).isEqualTo("SUCCESS");
        verify(paymentGateway, times(3)).processPayment(any());
    }
}
