/* @MENTEE_POWER (C)2026 */
package ru.mentee.product.service;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.springframework.stereotype.Service;
import ru.mentee.product.api.dto.RecommendationDto;

@Service
public class RecommendationService {

    public CompletableFuture<List<RecommendationDto>> getRecommendations(String category) {
        return CompletableFuture.completedFuture(
                Arrays.asList(
                        RecommendationDto.builder()
                                .productId("rec1")
                                .name("Recommended Product 1")
                                .price(89.99)
                                .build(),
                        RecommendationDto.builder()
                                .productId("rec2")
                                .name("Recommended Product 2")
                                .price(79.99)
                                .build()));
    }
}
