package com.eTrust.product.factory;

import com.eTrust.product.dto.request.ProductRequest;
import com.eTrust.product.mapper.ProductMapper;
import com.eTrust.product.model.Product;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;


@Component
public class ProductFactory {

    private final ProductMapper mapper;

    public ProductFactory(ProductMapper mapper) {
        this.mapper = mapper;
    }

    public Product createFrom(ProductRequest request) {
        Product product = mapper.toDomain(request);
        long now = Instant.now().getEpochSecond();
        product.setInternalReference("REF-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        product.setCreatedAt(now);
        product.setUpdatedAt(now);
        return product;
    }

    public Product createFrom(ProductRequest request, String base64Image) {
        // Create new record with updated image since records are immutable
        ProductRequest withImage = new ProductRequest(
                request.code(), request.name(), request.description(),
                base64Image, request.category(), request.price(),
                request.quantity(), request.shellId(),
                request.inventoryStatus(), request.rating()
        );
        return createFrom(withImage);
    }
}
