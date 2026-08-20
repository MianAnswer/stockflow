package com.miananswer.stockflow.model.dto;

import com.miananswer.stockflow.model.entity.InventoryTransactionType;

import java.time.Instant;

public record InventoryTransactionResponse(
        Long id,
        Long productId,
        InventoryTransactionType type,
        Integer quantity,
        Instant createdAt
) {
}
