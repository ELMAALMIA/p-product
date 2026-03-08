package com.eTrust.product.repository;

import com.eTrust.product.entity.ProductEntity;
import com.eTrust.product.entity.InventoryStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<ProductEntity, Long> {

    Optional<ProductEntity> findByCode(String code);

    boolean existsByCode(String code);

    @Query("""
            SELECT p FROM ProductEntity p
            WHERE (:status IS NULL OR p.inventoryStatus = :status)
            AND (:category IS NULL OR p.category = :category)
            """)
    List<ProductEntity> findByFilters(
            @Param("status") InventoryStatus status,
            @Param("category") String category
    );
}