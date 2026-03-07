package com.eTrust.product.service.impl;

import com.eTrust.product.dto.request.ProductRequest;
import com.eTrust.product.dto.response.ProductResponse;
import com.eTrust.product.enums.InventoryStatus;
import com.eTrust.product.service.ProductService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * Decorator pattern — wraps ProductServiceImpl with image size validation.
 * @Primary so Spring injects this into the controller automatically.
 */
@Service
@Primary
public class ValidatedProductService implements ProductService {

    private static final long MAX_IMAGE_BYTES = 100 * 1024L;

    private final ProductService delegate;

    public ValidatedProductService(@Qualifier("productServiceImpl") ProductService delegate) {
        this.delegate = delegate;
    }

    @Override
    public ProductResponse create(ProductRequest request) {
        validateBase64Image(request.image());
        return delegate.create(request);
    }

    @Override
    public ProductResponse findById(Long id) {
        return delegate.findById(id);
    }

    @Override
    public List<ProductResponse> findAll(InventoryStatus status, String category) {
        return delegate.findAll(status, category);
    }

    @Override
    public ProductResponse update(Long id, ProductRequest request) {
        validateBase64Image(request.image());
        return delegate.update(id, request);
    }

    @Override
    public void delete(Long id) {
        delegate.delete(id);
    }



    private void validateBase64Image(String base64Image) {
        if (base64Image == null || base64Image.isBlank()) return;
        String data = base64Image.contains(",")
                ? base64Image.substring(base64Image.indexOf(',') + 1)
                : base64Image;
        if ((long) (data.length() * 0.75) > MAX_IMAGE_BYTES)
            throw new IllegalArgumentException("Image must be less than 100KB");
    }
}
