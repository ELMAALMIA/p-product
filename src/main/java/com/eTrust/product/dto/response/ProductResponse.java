package com.eTrust.product.dto.response;

import com.eTrust.product.entity.InventoryStatus;

import java.time.Instant;

public record ProductResponse(
        Long id,
        String code,
        String name,
        String description,
        String image,
        String category,
        Double price,
        Integer quantity,
        String internalReference,
        Long shellId,
        InventoryStatus inventoryStatus,
        Double rating,
        Instant createdAt,
        Instant updatedAt
) {}