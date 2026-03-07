package com.eTrust.product.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class ProductEventListener {

    private static final Logger log = LoggerFactory.getLogger(ProductEventListener.class);

    @EventListener
    public void onProductCreated(ProductCreatedEvent event) {
        log.info("[EVENT] Product created — id={} code={} ref={}",
                event.getProduct().getId(),
                event.getProduct().getCode(),
                event.getProduct().getInternalReference());
    }

    @EventListener
    public void onProductDeleted(ProductDeletedEvent event) {
        log.info("[EVENT] Product deleted — id={}", event.getProductId());
    }
}
