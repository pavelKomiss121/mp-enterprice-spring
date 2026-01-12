/* @MENTEE_POWER (C)2026 */
package ru.mentee.product.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mentee.product.api.dto.ProductDetailsDto;
import ru.mentee.product.service.ProductAggregationService;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Tag(
        name = "Product Details API",
        description = "API для получения детальной информации о продукте")
public class ProductController {

    private final ProductAggregationService aggregationService;

    @GetMapping("/{productId}/details")
    @Operation(summary = "Получить детальную информацию о продукте")
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Детальная информация",
                        content =
                                @Content(
                                        schema =
                                                @Schema(implementation = ProductDetailsDto.class))),
                @ApiResponse(responseCode = "404", description = "Продукт не найден")
            })
    public ResponseEntity<ProductDetailsDto> getProductDetails(
            @Parameter(description = "ID продукта", required = true) @PathVariable
                    String productId) {
        try {
            CompletableFuture<ProductDetailsDto> future =
                    aggregationService.getProductDetails(productId);
            ProductDetailsDto result = future.get();
            if (result.getProductInfo() == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
