package com.eTrust.product.event;

import com.eTrust.product.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class ProductEventListener {

    private static final Logger log = LoggerFactory.getLogger(ProductEventListener.class);

    private final EmailService emailService;

    public ProductEventListener(EmailService emailService) {
        this.emailService = emailService;
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onProductCreated(ProductCreatedEvent event) {
        log.info("[EVENT] Product created — id={} code={}",
                event.getProduct().getId(),
                event.getProduct().getCode());
        emailService.sendProductCreatedNotification(
                event.getUserEmail(),
                event.getProduct().getName(),
                event.getProduct().getCode()
        );
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onProductDeleted(ProductDeletedEvent event) {
        log.info("[EVENT] Product deleted — id={}", event.getProductId());
        emailService.sendProductDeletedNotification(event.getUserEmail(), event.getProductId());
    }
}
