/* @MENTEE_POWER (C)2026 */
package ru.mentee.product.service;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.mentee.product.api.dto.InventoryDto;
import ru.mentee.product.api.dto.ProductDetailsDto;
import ru.mentee.product.api.dto.ProductInfoDto;
import ru.mentee.product.api.dto.RecommendationDto;
import ru.mentee.product.api.dto.ReviewDto;

@Service
public class ProductAggregationService {

    private static final int TIMEOUT_SECONDS = 2;

    private final ProductInfoService productInfoService;
    private final ReviewService reviewService;
    private final InventoryService inventoryService;
    private final RecommendationService recommendationService;
    private final Executor productTaskExecutor;

    public ProductAggregationService(
            ProductInfoService productInfoService,
            ReviewService reviewService,
            InventoryService inventoryService,
            RecommendationService recommendationService,
            @Qualifier("productTaskExecutor") Executor productTaskExecutor) {
        this.productInfoService = productInfoService;
        this.reviewService = reviewService;
        this.inventoryService = inventoryService;
        this.recommendationService = recommendationService;
        this.productTaskExecutor = productTaskExecutor;
    }

    public CompletableFuture<ProductDetailsDto> getProductDetails(String productId) {
        CompletableFuture<ProductInfoDto> productInfoFuture =
                productInfoService.getProductInfo(productId);

        return productInfoFuture
                .thenComposeAsync(
                        productInfo -> {
                            CompletableFuture<List<ReviewDto>> reviewsFuture =
                                    handleFutureWithFallback(
                                            reviewService.getReviews(productId),
                                            Collections.emptyList());
                            CompletableFuture<List<InventoryDto>> inventoryFuture =
                                    handleFutureWithFallback(
                                            inventoryService.getInventory(productId),
                                            Collections.emptyList());
                            CompletableFuture<List<RecommendationDto>> recommendationsFuture =
                                    handleFutureWithFallback(
                                            recommendationService.getRecommendations(
                                                    productInfo.getCategory()),
                                            Collections.emptyList());

                            return CompletableFuture.allOf(
                                            reviewsFuture, inventoryFuture, recommendationsFuture)
                                    .thenApplyAsync(
                                            v -> {
                                                try {
                                                    return ProductDetailsDto.builder()
                                                            .productInfo(productInfo)
                                                            .reviews(reviewsFuture.get())
                                                            .inventory(inventoryFuture.get())
                                                            .recommendations(
                                                                    recommendationsFuture.get())
                                                            .build();
                                                } catch (Exception e) {
                                                    return ProductDetailsDto.builder()
                                                            .productInfo(productInfo)
                                                            .reviews(Collections.emptyList())
                                                            .inventory(Collections.emptyList())
                                                            .recommendations(
                                                                    Collections.emptyList())
                                                            .build();
                                                }
                                            },
                                            productTaskExecutor);
                        },
                        productTaskExecutor)
                .exceptionally(
                        throwable -> {
                            return ProductDetailsDto.builder()
                                    .productInfo(null)
                                    .reviews(Collections.emptyList())
                                    .inventory(Collections.emptyList())
                                    .recommendations(Collections.emptyList())
                                    .build();
                        });
    }

    private <T> CompletableFuture<T> handleFutureWithFallback(
            CompletableFuture<T> future, T fallback) {
        return future.orTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .handle(
                        (result, throwable) -> {
                            if (throwable != null) {
                                return fallback;
                            }
                            return result;
                        });
    }
}
