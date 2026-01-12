/* @MENTEE_POWER (C)2024 */
package ru.mentee.ecommerce.dto;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {
    private Long id;
    private String customerName;
    private LocalDate orderDate;
    private Integer itemsCount;
}
