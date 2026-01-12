/* @MENTEE_POWER (C)2026 */
package ru.mentee.product.api.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDetailsDto {
    private ProductInfoDto productInfo;

    @Builder.Default private List<ReviewDto> reviews = new ArrayList<>();

    @Builder.Default private List<InventoryDto> inventory = new ArrayList<>();

    @Builder.Default private List<RecommendationDto> recommendations = new ArrayList<>();
}
