package com.eTrust.product.mapper;

import com.eTrust.product.dto.request.ProductRequest;
import com.eTrust.product.dto.response.ProductResponse;
import com.eTrust.product.enums.InventoryStatus;
import com.eTrust.product.model.Product;
import org.springframework.stereotype.Component;

/**
 * Mapper pattern — converts between Record DTOs and domain model.
 *
 * Records are immutable so toResponse() uses the canonical constructor.
 * toDomain() reads via record accessors (no "get" prefix).
 */
@Component
public class ProductMapper {

    public Product toDomain(ProductRequest request) {
        if (request == null) return null;

        Product product = new Product();
        product.setCode(request.code());           // record accessor — no get
        product.setName(request.name());
        product.setDescription(request.description());
        product.setImage(request.image());
        product.setCategory(request.category());
        product.setPrice(request.price());
        product.setQuantity(request.quantity());
        product.setShellId(request.shellId());
        product.setInventoryStatus(
            request.inventoryStatus() != null
                ? request.inventoryStatus()
                : InventoryStatus.INSTOCK
        );
        product.setRating(request.rating() != null ? request.rating() : 0.0);
        return product;
    }

    public ProductResponse toResponse(Product product) {
        if (product == null) return null;

        // Record constructor — all fields passed positionally
        return new ProductResponse(
                product.getId(),
                product.getCode(),
                product.getName(),
                product.getDescription(),
                product.getImage(),
                product.getCategory(),
                product.getPrice(),
                product.getQuantity(),
                product.getInternalReference(),
                product.getShellId(),
                product.getInventoryStatus(),
                product.getRating(),
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }

    public void updateDomainFromRequest(ProductRequest request, Product product) {
        product.setCode(request.code());
        product.setName(request.name());
        product.setDescription(request.description());
        product.setImage(request.image());
        product.setCategory(request.category());
        product.setPrice(request.price());
        product.setQuantity(request.quantity());
        product.setShellId(request.shellId());
        if (request.inventoryStatus() != null) {
            product.setInventoryStatus(request.inventoryStatus());
        }
        if (request.rating() != null) {
            product.setRating(request.rating());
        }
    }
}
