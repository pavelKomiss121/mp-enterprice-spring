/* @MENTEE_POWER (C)2026 */
package ru.mentee.product.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import ru.mentee.product.ProductApplication;
import ru.mentee.product.api.dto.InventoryDto;
import ru.mentee.product.api.dto.ProductDetailsDto;
import ru.mentee.product.api.dto.ProductInfoDto;
import ru.mentee.product.api.dto.RecommendationDto;
import ru.mentee.product.api.dto.ReviewDto;

@SpringBootTest(classes = ProductApplication.class)
@TestPropertySource(
        properties = {
            "spring.liquibase.enabled=false",
            "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration,org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration"
        })
class ProductAggregationServiceTest {

    @Autowired private ProductAggregationService aggregationService;

    @MockBean private ProductInfoService productInfoService;

    @MockBean private ReviewService reviewService;

    @MockBean private InventoryService inventoryService;

    @MockBean private RecommendationService recommendationService;

    @Test
    @DisplayName("Should корректно скомбинировать все данные")
    void shouldCombineAllDataCorrectly() throws Exception {
        // Given
        ProductInfoDto productInfo =
                ProductInfoDto.builder()
                        .productId("123")
                        .name("Test Product")
                        .description("Test Description")
                        .category("Electronics")
                        .price(99.99)
                        .build();

        List<ReviewDto> reviews =
                Arrays.asList(
                        ReviewDto.builder()
                                .reviewId("review1")
                                .userId("user1")
                                .comment("Great product!")
                                .rating(5)
                                .build());

        List<InventoryDto> inventory =
                Arrays.asList(
                        InventoryDto.builder()
                                .warehouseId("warehouse1")
                                .warehouseName("Main Warehouse")
                                .quantity(100)
                                .build());

        List<RecommendationDto> recommendations =
                Arrays.asList(
                        RecommendationDto.builder()
                                .productId("rec1")
                                .name("Recommended Product 1")
                                .price(89.99)
                                .build());

        when(productInfoService.getProductInfo("123"))
                .thenReturn(CompletableFuture.completedFuture(productInfo));
        when(reviewService.getReviews("123"))
                .thenReturn(CompletableFuture.completedFuture(reviews));
        when(inventoryService.getInventory("123"))
                .thenReturn(CompletableFuture.completedFuture(inventory));
        when(recommendationService.getRecommendations("Electronics"))
                .thenReturn(CompletableFuture.completedFuture(recommendations));

        // When
        CompletableFuture<ProductDetailsDto> future = aggregationService.getProductDetails("123");

        // Then
        ProductDetailsDto result = future.get();
        assertThat(result.getProductInfo()).isNotNull();
        assertThat(result.getReviews()).isNotEmpty();
        assertThat(result.getInventory()).isNotEmpty();
        assertThat(result.getRecommendations()).isNotEmpty();
    }

    @Test
    @DisplayName("Should вернуть данные даже если рекомендации упали с таймаутом")
    void shouldReturnDataWhenRecommendationsFail() throws Exception {
        // Given
        ProductInfoDto productInfo =
                ProductInfoDto.builder()
                        .productId("123")
                        .name("Test Product")
                        .description("Test Description")
                        .category("Electronics")
                        .price(99.99)
                        .build();

        List<ReviewDto> reviews =
                Arrays.asList(
                        ReviewDto.builder()
                                .reviewId("review1")
                                .userId("user1")
                                .comment("Great product!")
                                .rating(5)
                                .build());

        List<InventoryDto> inventory =
                Arrays.asList(
                        InventoryDto.builder()
                                .warehouseId("warehouse1")
                                .warehouseName("Main Warehouse")
                                .quantity(100)
                                .build());

        when(productInfoService.getProductInfo("123"))
                .thenReturn(CompletableFuture.completedFuture(productInfo));
        when(reviewService.getReviews("123"))
                .thenReturn(CompletableFuture.completedFuture(reviews));
        when(inventoryService.getInventory("123"))
                .thenReturn(CompletableFuture.completedFuture(inventory));
        when(recommendationService.getRecommendations(any()))
                .thenReturn(CompletableFuture.failedFuture(new TimeoutException()));

        // When
        CompletableFuture<ProductDetailsDto> future = aggregationService.getProductDetails("123");

        // Then
        ProductDetailsDto result = future.get();
        assertThat(result.getProductInfo()).isNotNull();
        assertThat(result.getReviews()).isNotEmpty();
        assertThat(result.getInventory()).isNotEmpty();
        // Проверяем, что рекомендации - пустой список (fallback)
        assertThat(result.getRecommendations()).isEmpty();
    }
}
