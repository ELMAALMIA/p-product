package com.eTrust.product.mapper;

import com.eTrust.product.entity.ProductEntity;
import com.eTrust.product.model.Product;
import org.springframework.stereotype.Component;
import org.modelmapper.ModelMapper;


@Component
public class ProductEntityMapper {

    private final ModelMapper modelMapper;

    public ProductEntityMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public Product toDomain(ProductEntity entity) {
        return modelMapper.map(entity, Product.class);
    }

    public ProductEntity toEntity(Product product) {
        return modelMapper.map(product, ProductEntity.class);
    }
}