package com.eTrust.product.mapper;

import com.eTrust.product.dao.ProductDocument;
import com.eTrust.product.model.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductDocumentMapper {

    public Product toDomain(ProductDocument doc) {
        if (doc == null) return null;
        Product p = new Product();
        p.setId(doc.getId() != null ? Long.parseLong(doc.getId()) : null);
        p.setCode(doc.getCode());
        p.setName(doc.getName());
        p.setDescription(doc.getDescription());
        p.setImage(doc.getImage());
        p.setCategory(doc.getCategory());
        p.setPrice(doc.getPrice());
        p.setQuantity(doc.getQuantity());
        p.setInternalReference(doc.getInternalReference());
        p.setShellId(doc.getShellId());
        p.setInventoryStatus(doc.getInventoryStatus());
        p.setRating(doc.getRating());
        p.setCreatedAt(doc.getCreatedAt());
        p.setUpdatedAt(doc.getUpdatedAt());
        return p;
    }

    public ProductDocument toDocument(Product p) {
        if (p == null) return null;
        ProductDocument doc = new ProductDocument();
        doc.setId(p.getId() != null ? String.valueOf(p.getId()) : null);
        doc.setCode(p.getCode());
        doc.setName(p.getName());
        doc.setDescription(p.getDescription());
        doc.setImage(p.getImage());
        doc.setCategory(p.getCategory());
        doc.setPrice(p.getPrice());
        doc.setQuantity(p.getQuantity());
        doc.setInternalReference(p.getInternalReference());
        doc.setShellId(p.getShellId());
        doc.setInventoryStatus(p.getInventoryStatus());
        doc.setRating(p.getRating());
        doc.setCreatedAt(p.getCreatedAt());
        doc.setUpdatedAt(p.getUpdatedAt());
        return doc;
    }
}
