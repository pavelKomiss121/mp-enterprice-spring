/* @MENTEE_POWER (C)2026 */
package ru.mentee.product.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.mentee.product.api.client.ProductClient;
import ru.mentee.product.api.dto.ProductDto;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductIntegrationService {

    private final ProductClient productClient;

    @CircuitBreaker(name = "product-service", fallbackMethod = "getProductFallback")
    @Retry(name = "product-service")
    public ProductDto getProduct(Long id) {
        return productClient.getProductById(id);
    }

    public ProductDto getProductFallback(Long id, Throwable t) {
        log.warn("Fallback for getProduct, id: {}, error: {}", id, t.getMessage());
        return ProductDto.builder()
                .id(id)
                .name("Cached Product Data")
                .description("Fallback product data")
                .price(0.0)
                .category("Unknown")
                .build();
    }
}
