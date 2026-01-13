/* @MENTEE_POWER (C)2026 */
package ru.mentee.product.api.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.mentee.product.api.dto.ProductDto;

@FeignClient(
        name = "product-service",
        url = "${feign.client.config.product-service.url:http://localhost:8080}",
        fallback = ProductClientFallback.class)
public interface ProductClient {
    @GetMapping("/api/products/{id}")
    ProductDto getProductById(@PathVariable Long id);
}
