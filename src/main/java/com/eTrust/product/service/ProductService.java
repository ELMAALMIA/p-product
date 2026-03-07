package com.eTrust.product.service;

import com.eTrust.product.dto.request.ProductRequest;
import com.eTrust.product.dto.response.ProductResponse;
import com.eTrust.product.enums.InventoryStatus;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ProductService {
    ProductResponse create(ProductRequest request);
    ProductResponse findById(Long id);
    List<ProductResponse> findAll(InventoryStatus status, String category);
    ProductResponse update(Long id, ProductRequest request);
    void delete(Long id);
//    ProductResponse createWithFile(ProductRequest request, MultipartFile image) throws IOException;
}
