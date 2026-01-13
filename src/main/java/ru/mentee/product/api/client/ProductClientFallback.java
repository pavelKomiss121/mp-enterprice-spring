/* @MENTEE_POWER (C)2026 */
package ru.mentee.product.api.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.mentee.product.api.dto.ProductDto;

@Slf4j
@Component
public class ProductClientFallback implements ProductClient {

    @Override
    public ProductDto getProductById(Long id) {
        log.warn("Fallback called for product id: {}", id);
        return ProductDto.builder()
                .id(id)
                .name("Cached Product Data")
                .description("Fallback product data")
                .price(0.0)
                .category("Unknown")
                .build();
    }
}
