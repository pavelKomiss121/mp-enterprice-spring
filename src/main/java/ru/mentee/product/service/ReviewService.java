/* @MENTEE_POWER (C)2026 */
package ru.mentee.product.service;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.springframework.stereotype.Service;
import ru.mentee.product.api.dto.ReviewDto;

@Service
public class ReviewService {

    public CompletableFuture<List<ReviewDto>> getReviews(String productId) {
        return CompletableFuture.completedFuture(
                Arrays.asList(
                        ReviewDto.builder()
                                .reviewId("review1")
                                .userId("user1")
                                .comment("Great product!")
                                .rating(5)
                                .build(),
                        ReviewDto.builder()
                                .reviewId("review2")
                                .userId("user2")
                                .comment("Good quality")
                                .rating(4)
                                .build()));
    }
}
