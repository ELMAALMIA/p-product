package com.eTrust.product.event;

import org.springframework.context.ApplicationEvent;

public class ProductDeletedEvent extends ApplicationEvent {

    private final Long productId;
    private final String userEmail;

    public ProductDeletedEvent(Object source, Long productId, String userEmail) {
        super(source);
        this.productId = productId;
        this.userEmail = userEmail;
    }

    public Long getProductId() { return productId; }

    public String getUserEmail() { return userEmail; }
}
