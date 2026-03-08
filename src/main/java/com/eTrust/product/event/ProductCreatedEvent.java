package com.eTrust.product.event;

import com.eTrust.product.entity.ProductEntity;
import org.springframework.context.ApplicationEvent;

public class ProductCreatedEvent extends ApplicationEvent {

    private final ProductEntity product;
    private final String userEmail;

    public ProductCreatedEvent(Object source, ProductEntity product, String userEmail) {
        super(source);
        this.product = product;
        this.userEmail = userEmail;
    }

    public ProductEntity getProduct() { return product; }

    public String getUserEmail() { return userEmail; }
}
