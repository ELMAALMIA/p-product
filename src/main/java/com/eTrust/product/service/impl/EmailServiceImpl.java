package com.eTrust.product.service.impl;

import com.eTrust.product.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailServiceImpl.class);

    private static final String SUBJECT_CREATED = "New Product Created";
    private static final String SUBJECT_DELETED  = "Product Deleted";
    private static final String BODY_CREATED     = "Product '%s' with code '%s' has been created.";
    private static final String BODY_DELETED     = "Product with id=%d has been deleted.";

    private final JavaMailSender mailSender;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendNotification(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
            log.info("[EMAIL] Sent to={} subject={}", to, subject);
        } catch (Exception e) {
            log.error("[EMAIL] Failed to send to={} reason={}", to, e.getMessage());
        }
    }

    @Override
    public void sendProductCreatedNotification(String to, String productName, String code) {
        sendNotification(to, SUBJECT_CREATED, String.format(BODY_CREATED, productName, code));
    }

    @Override
    public void sendProductDeletedNotification(String to, Long productId) {
        sendNotification(to, SUBJECT_DELETED, String.format(BODY_DELETED, productId));
    }
}