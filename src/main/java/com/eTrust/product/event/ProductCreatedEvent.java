package com.eTrust.product.event;


import org.springframework.context.ApplicationEvent;
import com.eTrust.product.entity.ProductEntity;

public class ProductCreatedEvent extends ApplicationEvent {

    private final ProductEntity product;

    public ProductCreatedEvent(Object source, ProductEntity product) {
        super(source);
        this.product = product;
    }

    public ProductEntity getProduct() { return product; }
}