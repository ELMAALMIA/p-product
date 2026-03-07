package com.eTrust.product.event;

import com.eTrust.product.model.Product;
import org.springframework.context.ApplicationEvent;

public class ProductCreatedEvent extends ApplicationEvent {
    private final Product product;
    public ProductCreatedEvent(Object source, Product product) {
        super(source);
        this.product = product;
    }
    public Product getProduct() { return product; }
}
