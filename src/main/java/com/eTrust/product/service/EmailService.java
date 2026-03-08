package com.eTrust.product.service;



public interface EmailService {
    void sendNotification(String to, String subject, String body);
    void sendProductCreatedNotification(String to, String productName, String code);
    void sendProductDeletedNotification(String to, Long productId);
}