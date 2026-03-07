package com.eTrust.product.mapper;

import com.eTrust.product.dao.ProductEntity;
import com.eTrust.product.model.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductEntityMapper {

    public Product toDomain(ProductEntity entity) {
        if (entity == null) return null;
        Product p = new Product();
        p.setId(entity.getId());
        p.setCode(entity.getCode());
        p.setName(entity.getName());
        p.setDescription(entity.getDescription());
        p.setImage(entity.getImage());
        p.setCategory(entity.getCategory());
        p.setPrice(entity.getPrice());
        p.setQuantity(entity.getQuantity());
        p.setInternalReference(entity.getInternalReference());
        p.setShellId(entity.getShellId());
        p.setInventoryStatus(entity.getInventoryStatus());
        p.setRating(entity.getRating());
        p.setCreatedAt(entity.getCreatedAt());
        p.setUpdatedAt(entity.getUpdatedAt());
        return p;
    }

    public ProductEntity toEntity(Product p) {
        if (p == null) return null;
        ProductEntity entity = new ProductEntity();
        entity.setId(p.getId());
        entity.setCode(p.getCode());
        entity.setName(p.getName());
        entity.setDescription(p.getDescription());
        entity.setImage(p.getImage());
        entity.setCategory(p.getCategory());
        entity.setPrice(p.getPrice());
        entity.setQuantity(p.getQuantity());
        entity.setInternalReference(p.getInternalReference());
        entity.setShellId(p.getShellId());
        entity.setInventoryStatus(p.getInventoryStatus());
        entity.setRating(p.getRating());
        // createdAt / updatedAt handled by @PrePersist / @PreUpdate
        return entity;
    }
}
