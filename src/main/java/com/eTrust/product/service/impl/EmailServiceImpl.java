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

    private final JavaMailSender mailSender;
    private final SimpleMailMessage emailTemplate;

    public EmailServiceImpl(JavaMailSender mailSender, SimpleMailMessage emailTemplate) {
        this.mailSender = mailSender;
        this.emailTemplate = emailTemplate;
    }

    @Override
    public void sendNotification(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage(emailTemplate);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
            log.info("[EMAIL] Sent to={} subject={}", to, subject);
        } catch (Exception e) {
            log.error("[EMAIL] Failed to send email to={} reason={}", to, e.getMessage());
        }
    }

    @Override
    public void sendProductCreatedNotification(String productName, String code) {
        sendNotification(
                "ayoubelmaalmi@gmail.com",
                "New Product Created",
                "Product '" + productName + "' with code '" + code + "' has been created."
        );

    }

    @Override
    public void sendProductDeletedNotification(Long productId) {
        sendNotification(
                "ayoubelmaalmi@gmail.com",
                "Product Deleted",
                "Product with id=" + productId + " has been deleted."
        );
    }
}