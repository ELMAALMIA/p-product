package com.eTrust.product.event;

import com.eTrust.product.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class ProductEventListener {

    private static final Logger log = LoggerFactory.getLogger(ProductEventListener.class);

    private final EmailService emailService;

    public ProductEventListener(EmailService emailService) {
        this.emailService = emailService;
    }

    @Async
    @EventListener
    public void onProductCreated(ProductCreatedEvent event) {
        log.info("[EVENT] Product created — id={} code={}",
                event.getProduct().getId(),
                event.getProduct().getCode());
        emailService.sendProductCreatedNotification(
                event.getProduct().getName(),
                event.getProduct().getCode()
        );
    }

    @Async
    @EventListener
    public void onProductDeleted(ProductDeletedEvent event) {
        log.info("[EVENT] Product deleted — id={}", event.getProductId());
        emailService.sendProductDeletedNotification(event.getProductId());
    }
}
