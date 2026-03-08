package com.eTrust.product.dto.request;

import com.eTrust.product.entity.InventoryStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;


public record ProductRequest(

        @NotBlank(message = "Code is mandatory")
        @Pattern(
            regexp = "^[a-zA-Z0-9][a-zA-Z0-9\\-_]{4,}$",
            message = "Code must start with alphanumeric and be at least 5 characters"
        )
        String code,

        @NotBlank(message = "Name is mandatory")
        String name,

        String description,

        String image,

        @NotBlank(message = "Category is mandatory")
        String category,

        @NotNull(message = "Price is mandatory")
        @DecimalMin(value = "0.0", inclusive = false, message = "Price must be positive")
        Double price,

        @NotNull(message = "Quantity is mandatory")
        @Min(value = 0, message = "Quantity must be >= 0")
        @Max(value = 40, message = "Quantity must not exceed 40")
        Integer quantity,

        Long shellId,

        InventoryStatus inventoryStatus,

        @DecimalMin(value = "0.0", message = "Rating must be >= 0")
        @DecimalMax(value = "5.0", message = "Rating must be <= 5")
        Double rating

) {
    public ProductRequest {
        if (inventoryStatus == null) inventoryStatus = InventoryStatus.INSTOCK;
        if (rating == null) rating = 0.0;
    }
}
