package com.eTrust.product.repository;

import com.eTrust.product.enums.InventoryStatus;
import com.eTrust.product.model.Product;

import java.util.List;
import java.util.Optional;

/**
 * Repository pattern — domain contract.
 * Service depends on this interface only. DB is a swappable detail.
 */
public interface ProductRepository {
    Product save(Product product);
    Optional<Product> findById(Long id);
    Optional<Product> findByCode(String code);
    boolean existsById(Long id);
    boolean existsByCode(String code);
    List<Product> findAll();
    List<Product> findByFilters(InventoryStatus status, String category);
    void deleteById(Long id);
}
