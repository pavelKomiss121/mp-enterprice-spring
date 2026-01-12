/* @MENTEE_POWER (C)2026 */
package ru.mentee.product.service;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.springframework.stereotype.Service;
import ru.mentee.product.api.dto.InventoryDto;

@Service
public class InventoryService {

    public CompletableFuture<List<InventoryDto>> getInventory(String productId) {
        return CompletableFuture.completedFuture(
                Arrays.asList(
                        InventoryDto.builder()
                                .warehouseId("warehouse1")
                                .warehouseName("Main Warehouse")
                                .quantity(100)
                                .build(),
                        InventoryDto.builder()
                                .warehouseId("warehouse2")
                                .warehouseName("Secondary Warehouse")
                                .quantity(50)
                                .build()));
    }
}
