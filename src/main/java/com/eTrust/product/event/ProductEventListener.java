package com.eTrust.product.event;

import com.eTrust.product.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.stereotype.Component;

@Component
public class ProductEventListener {


/*
Notifications: envoyer un email / Slack / webhook “product created/deleted”.
Indexation / Search: pousser l’info vers Elasticsearch / OpenSearch pour la recherche.
Cache: invalider/rafraîchir un cache (ex: Redis) après création/suppression.
Analytics: publier un event vers Kafka/RabbitMQ pour stats (nombre de produits créés, catégories, etc.).
Synchronisation inter-services: si tu as d’autres services (inventory, pricing), déclencher une mise à jour asynchrone.
Image processing: si image est base64, déclencher un job asynchrone (resize/compression) et stocker ailleurs, après commit.
*/
    private static final Logger log = LoggerFactory.getLogger(ProductEventListener.class);
    private final EmailService emailService;

    public ProductEventListener(EmailService emailService) {
        this.emailService = emailService;
    }
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onProductCreated(ProductCreatedEvent event) {
        log.info("[EVENT] Product created — id={} code={} ref={}",
                event.getProduct().getId(),
                event.getProduct().getCode(),
                event.getProduct().getInternalReference());
        emailService.sendProductCreatedNotification(
                event.getProduct().getName(),
                event.getProduct().getCode()
        );
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onProductDeleted(ProductDeletedEvent event) {
        log.info("[EVENT] Product deleted — id={}", event.getProductId());
        emailService.sendProductDeletedNotification(
                event.getProductId()
        );
    }
}
