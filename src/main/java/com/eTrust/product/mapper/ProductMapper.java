package com.eTrust.product.mapper;

import com.eTrust.product.dto.request.ProductRequest;
import com.eTrust.product.dto.response.ProductResponse;
import com.eTrust.product.entity.ProductEntity;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    private final ModelMapper modelMapper;

    public ProductMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public ProductEntity toEntity(ProductRequest request) {
        return modelMapper.map(request, ProductEntity.class);
    }

    public ProductResponse toResponse(ProductEntity e) {
        return new ProductResponse(
                e.getId(),
                e.getCode(),
                e.getName(),
                e.getDescription(),
                e.getImage(),
                e.getCategory(),
                e.getPrice(),
                e.getQuantity(),
                e.getInternalReference(),
                e.getShellId(),
                e.getInventoryStatus(),
                e.getRating(),
                e.getCreatedAt(),
                e.getUpdatedAt()
        );
    }

    public void updateEntityFromRequest(ProductRequest request, ProductEntity entity) {
        modelMapper.map(request, entity);
    }
}