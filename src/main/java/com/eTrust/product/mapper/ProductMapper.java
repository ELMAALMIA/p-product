package com.eTrust.product.mapper;

import com.eTrust.product.dto.request.ProductRequest;
import com.eTrust.product.dto.response.ProductResponse;
import com.eTrust.product.model.Product;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    private final ModelMapper modelMapper;

    public ProductMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public Product toDomain(ProductRequest request) {
        return modelMapper.map(request, Product.class);
    }


    public ProductResponse toResponse(Product p) {
        return new ProductResponse(
                p.getId(),
                p.getCode(),
                p.getName(),
                p.getDescription(),
                p.getImage(),
                p.getCategory(),
                p.getPrice(),
                p.getQuantity(),
                p.getInternalReference(),
                p.getShellId(),
                p.getInventoryStatus(),
                p.getRating(),
                p.getCreatedAt(),
                p.getUpdatedAt()
        );
    }


    public void updateDomainFromRequest(ProductRequest request, Product product) {
        modelMapper.map(request, product);
    }
}