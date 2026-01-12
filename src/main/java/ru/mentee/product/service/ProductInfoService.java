/* @MENTEE_POWER (C)2026 */
package ru.mentee.product.service;

import java.util.concurrent.CompletableFuture;
import org.springframework.stereotype.Service;
import ru.mentee.product.api.dto.ProductInfoDto;

@Service
public class ProductInfoService {

    public CompletableFuture<ProductInfoDto> getProductInfo(String productId) {
        return CompletableFuture.completedFuture(
                ProductInfoDto.builder()
                        .productId(productId)
                        .name("Product " + productId)
                        .description("Description for product " + productId)
                        .category("Electronics")
                        .price(99.99)
                        .build());
    }
}
