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
public class ProductInfoDto {
    private String productId;
    private String name;
    private String description;
    private String category;
    private Double price;
}
