/* @MENTEE_POWER (C)2025 */
package ru.mentee.banking.service;

import org.springframework.stereotype.Component;
import ru.mentee.banking.api.dto.PaymentRequest;

@Component
public class PaymentGateway {
    public ExternalPaymentService.PaymentResult processPayment(PaymentRequest request) {
        return new ExternalPaymentService.PaymentResult("SUCCESS");
    }
}
