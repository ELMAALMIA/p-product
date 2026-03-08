package com.eTrust.product.service;



public interface EmailService {
    void sendNotification(String to, String subject, String body);
    void sendProductCreatedNotification(String productName, String code);
    void sendProductDeletedNotification(Long productId);
}