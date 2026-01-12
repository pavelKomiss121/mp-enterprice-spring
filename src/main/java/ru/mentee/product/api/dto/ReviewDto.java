/* @MENTEE_POWER (C)2026 */
package ru.mentee.product.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDto {
    private String reviewId;
    private String userId;
    private String comment;
    private Integer rating;
}
